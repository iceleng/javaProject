import org.apache.log4j.Logger
import groovy.json.JsonSlurper

log = Logger.getLogger("ScanMovies")
log.info "action=start"

//def config = new ConfigSlurper("configure").parse(new File('Configure.groovy').toURL())  //此种设置会导致中文乱码，命令执行失败。
def config = new ConfigSlurper("configure").parse(new File('Configure.groovy').text)  

err = new StringWriter()
out = new StringWriter()
def screenConfig=config.screenConfig
log.debug "action=screenConfig result=\"${screenConfig}\""
if (!config.testFlag){
	def count=0
	new File(config.movieDir).traverse(type:groovy.io.FileType.FILES,nameFilter:~/(?i).*\.(${config.movieExt})/){
		log.info "action=scanFolder count=${++count} info=\"${it}\""
		//montage 设置覆盖
		screenConfig.titleFlag=true
		screenConfig.watermarkFlag=false
		screenConfig.videoJson=probeMovie(config.ffprobe,it.toString())
		screenConfig.screenNumber=4
		screenConfig.tile="2x2"
		screenConfig.screenWidth=500
		processVideo(screenConfig,new File("${it.toString()}_montage.jpg"))
		//preview 设置覆盖
		screenConfig.titleFlag=false
		screenConfig.videoJson=probeMovie(config.ffprobe,it.toString())
		screenConfig.screenNumber=10
		screenConfig.tile="10x1"
		processVideo(screenConfig,new File("${it.toString()}_preview.jpg"))
	}
}else{
	screenConfig.titleFlag=true
	screenConfig.watermarkFlag=false
	screenConfig.videoJson=probeMovie(config.ffprobe,config.movieFile)
	screenConfig.screenNumber=4
	screenConfig.tile="2x2"
	screenConfig.screenWidth=500
	processVideo(screenConfig,new File("${config.movieFile}_montage.jpg"))
}

def processVideo(screenConfig,targetFile){
	if (!targetFile.exists()){
		def montageFile=montageProcess(screenConfig,genMovieScreenShot(screenConfig))
		if (montageFile.exists()){
			montageFile.renameTo(targetFile)
			log.info "action=rename info=\"${targetFile}\""
		}else{
			log.error "action=noneExists info=\"${targetFile}\""
		}
	}else{
		log.info "action=exists info=\"${targetFile} exists.\""
	}	
}

//获取视频的信息，返回json格式
def probeMovie(ffprobe,movieFile){
	def cmd,proc,result
	//使用ffprobe获取视频信息，json格式
	cmd="\"${ffprobe}\" -v quiet -print_format json -show_format -show_streams \"${movieFile}\""
	log.info  "action=ffprobe info=\"get video meta infomations.\""
	log.debug "action=ffprobe cmd=\"${cmd}\""
	proc=cmd.execute()
	//输出inputStream的编码格式：println new InputStreamReader(proc.in).getEncoding()，要用utf-8，不然中文会乱码
	result=new InputStreamReader(proc.in,"utf-8").text
	log.debug "action=ffprobe result=\"${result}\""
	proc.waitFor()
	return new JsonSlurper().parseText(result)
}

//生成视频截图，返回montage需要的所有图片路径
def genMovieScreenShot(screenConfig){
	log.info "action=screenshot info=\"begin generate preview of selected video.\""
	def movieFile=screenConfig.videoJson.format.filename
	def totalScreenShot=screenConfig.screenNumber
	//1-0.118主要是为了过滤最后的结束画面
	def averageSeconds=(screenConfig.videoJson.format.duration as BigDecimal)*(1-0.05)/totalScreenShot as int 
	def picsString=""
	(1..totalScreenShot).each{
		def screenshotFile="${screenConfig.workspace}${it}.jpg"
		def cmd=""
		//因为ffmpeg截取rmvb视频会出现大量的色块，所以换用mplayer截图
		if (movieFile.toLowerCase().endsWith("rmvb")){
			cmd="\"${screenConfig.mplayer}\" \"${movieFile}\" -ss ${it*averageSeconds} -noframedrop -nosound -vf scale=${screenConfig.screenWidth}:-3 -vo jpeg -frames 1 "
			cmd.execute().waitForProcessOutput( out, err )
			//因为mplayer截图不能指定文件名，所以需要先删除已有序列文件，然后再重命名
			if (new File("${it}.jpg").exists()) new File("${it}.jpg").delete()
			new File("00000001.jpg").renameTo("${screenConfig.workspace}${it}.jpg")
		}else{
			cmd="\"${screenConfig.ffmpeg}\" -ss ${it*averageSeconds} -i \"${movieFile}\" -vf \"scale=${screenConfig.screenWidth}:-1\" -f image2 -t 0.001 -y ${screenshotFile}"
			cmd.execute().waitForProcessOutput( out, err )
		}
		log.debug "action=screenshot cmd=${cmd}"
		//给所有图片打上时间戳水印
		def timestamp=new GregorianCalendar(0,0,0,0,0,(it*averageSeconds as BigDecimal)/1 as int,0).time.format('HH:mm:ss')
		cmd="\"${screenConfig.convert}\" ${screenshotFile} -gravity ${screenConfig.timestampGravity} -font ${screenConfig.timestampFont} -stroke ${screenConfig.timestampStrokeColor} -strokeWidth ${screenConfig.timestampStrokeWidth} -pointSize ${screenConfig.timestampPointSize} -annotate 0 \"${timestamp}\" -stroke none -fill ${screenConfig.timestampStrokeFill} -annotate 0 \"${timestamp}\" ${screenshotFile}"
		cmd.execute().waitForProcessOutput( out, err )
		picsString=picsString+"${new File(screenshotFile).absolutePath} "
	}
	return picsString
}

//返回最后合成的文件名
def montageProcess(screenConfig,picsString){
	//使用montage合并图片。title如果有中文，必须用文件的方式读取，指定font，注意，文件必须要UTF-8编码，其和字体文件可以不用指明路径，但是原始图片必须指明路径！
	def montageResult="${screenConfig.workspace}montage.jpg"

	def cmd="\"${screenConfig.montage}\" ${picsString} -background ${screenConfig.montageBackground} -geometry +0+0 -tile ${screenConfig.tile} -frame ${screenConfig.montageFrame} \"${montageResult}\""
	log.info "action=montage info=\"begin montage preview pic.\""
	log.debug "action=montage cmd=${cmd}"
	cmd.execute().waitForProcessOutput(out,err)
	//如果titleFlag为真则加video meta和水印，否则只montage图片。
	if (screenConfig.titleFlag){
		//选出视频流
		def movieJson=screenConfig.videoJson
		def movie_stream=movieJson.streams.find{
			it.codec_type=='video'
		}

		def movie_file_name=new File(movieJson.format.filename).name
		def movie_duration=movieJson.format.duration as BigDecimal
		def movie_size=(movieJson.format.size as long)/1204/1024 as int
		def title="""
		视频名称：${movie_file_name}\\n
		视屏大小：${movie_size} MB　　分辨率：${movie_stream.width} x ${movie_stream.height}　　长宽比：${movie_stream.display_aspect_ratio}　　帧/秒：${Eval.me(movie_stream.avg_frame_rate) as int}　　流数：${movieJson.format.nb_streams}　　码率：${(movieJson.format.bit_rate as long)/1024 as int} KB\\n
		播放时长：${new GregorianCalendar(0,0,0,0,0,(movieJson.format.duration as BigDecimal)/1 as int,0).time.format('HH:mm:ss')}　　创建时间：${movieJson?.format?.tags?.creation_time} 　　视频编码：${movie_stream.codec_name}　　封装格式：${movieJson.format.format_name}  
		""".toString()
		log.debug "action=genTitle info=${title}"

		cmd="\"${screenConfig.convert}\" \"${montageResult}\" -background ${screenConfig.watermarkBackground} -pointSize ${screenConfig.watermarkPointSize} -font ${screenConfig.watermarkFont} label:\"${title}\" -gravity ${screenConfig.watermarkGravity} +swap -append \"${montageResult}\""
		log.info  "action=convert info=\"add movie title info.\""
		log.debug "action=convert cmd=${cmd}"
		cmd.execute().waitForProcessOutput(out,err)

		//合成水印和剧情图
		if (screenConfig.watermarkFlag){
			cmd="\"${screenConfig.composite}\" -dissolve 60 -gravity NorthEast -geometry +0+0 watermark_shadow.png ${montageResult} ${montageResult}"
			log.info "action=composite cmd=\"${cmd}.\""
			cmd.execute().waitForProcessOutput(out,err)
		}
	}

	//清理生成的缩略图，不然rename会出问题
	def delCmd="cmd /c del ${picsString}"
	log.debug "action=delete cmd=\"${delCmd}\""
	delCmd.execute().waitForProcessOutput(out,err)
	return new File(montageResult)
}

log.info "action=end info=\"successful complete.\""
