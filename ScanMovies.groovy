import org.apache.log4j.Logger
import groovy.json.JsonSlurper

log = Logger.getLogger("ScanMovies")
log.info "action=start info=\"begin scan movies\""

workspace="./output/"
//===ffmpeg路径设定===
ffmpegDir="I:/development/ffmpeg-20150304-git-7da7d26-win64-static/bin/"
ffmpeg=ffmpegDir+"ffmpeg.exe"
ffprobe=ffmpegDir+"ffprobe.exe"
//===montage路径设定===
montage="D:/Program Files/ImageMagick-6.9.1-Q16/montage.exe"
convert="D:/Program Files/ImageMagick-6.9.1-Q16/convert.exe"
composite="D:/Program Files/ImageMagick-6.9.1-Q16/composite.exe"
//===mplayer路径设置===
mplayer="./mplayer.exe"
//===待分析电影路径===
movieDir="J:/backup/hmovie/201405"
movieDir="J:/backup/卡通"
//movieFile="J:/backup/done/[阳光电影www.ygdy8.com].哆啦A梦：伴我同行.BD.720p.国粤日三语中字.mkv"
movieFile="J:/backup/hmovie/201508/20150809_7_高清_有码_丰满大胸美女各种主动_PPPD298.mkv"
movieFile="J:/backup/hmovie/201409/20140913_8_标清_无码_高诚_加勒比群P_txmsf.rmvb"

err = new StringWriter()
out = new StringWriter()

def testFlag=false

if (!testFlag){
	def movieExt="avi|mpeg|mp4|flv|rm|rmvb|mkv|wmv"
	def count=0
	new File(movieDir).traverse(type:groovy.io.FileType.FILES,nameFilter:~/(?i).*\.(${movieExt})/){
		count++
		log.info "${count}\t${it}"
		def movieFile=it.toString()
		def targetFile=new File("${movieFile}_montage.jpg")
		if (!targetFile.exists()){
			def movieJson=probeMovie(movieFile)
			def montageFile=new File(montage(genMovieScreenShot(movieJson,movieFile),movieJson))
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
}else{
	//生成视频截图并合并到单张图。
	def movieJson=probeMovie(movieFile)
	def montageFile=new File(montage(genMovieScreenShot(movieJson,movieFile),movieJson))
	if (montageFile.exists()){
		def targetFile=new File("${movieFile}_montage.jpg")
		log.info "action=rename info=\"${targetFile}\""
		//如果原文件存在，会导致renameTo失败。
		if (targetFile.exists()){
			targetFile.delete()
			log.info "action=delete info=\"${targetFile} is exist. delete.\""
		}
		montageFile.renameTo(targetFile)
	}
}
//获取视频的信息，返回json格式
def probeMovie(movieFile){
	def cmd,proc,result
	//使用ffprobe获取视频信息，json格式
	cmd="\"${ffprobe}\" -v quiet -print_format json -show_format -show_streams \"${movieFile}\""
	log.info "action=\"get video info (json)\" info=\"${movieFile}\""
	log.debug "action=ffprobe info=\"${cmd}\""
	proc=cmd.execute()
	//输出inputStream的编码格式：println new InputStreamReader(proc.in).getEncoding()，要用utf-8，不然中文会乱码
	result=new InputStreamReader(proc.in,"utf-8").text
	log.debug "action=\"get movie json\" result=\"${result}\""
	proc.waitFor()
	return result
}

//生成视频截图，返回montage需要的所有图片路径
def genMovieScreenShot(json,movieFile){
	//解析json
	def movieInfoJson = new JsonSlurper().parseText(json)
	log.info "action=screenshot info=\"begin generate preview pic.\""
	def totalSeconds=movieInfoJson.format.duration as BigDecimal
	def totalScreenShot=16
	def averageSeconds=(totalSeconds)*(1-0.118)/(totalScreenShot-1) as int
	//生成16张视频缩略图，并生成montage拼接所有图的图片路径字符串
	def picsString=""
	(1..totalScreenShot).each{
		def screenshotFile="${workspace}${it}.jpg"
		def cmd=""
		//因为ffmpeg截取rmvb视频会出现大量的色块，所以换用mplayer截图
		if (movieFile.toLowerCase().endsWith("rmvb")){
			cmd="\"${mplayer}\" \"${movieFile}\" -ss ${it*averageSeconds} -noframedrop -nosound -vf scale=415:-3 -vo jpeg -frames 1 "
			cmd.execute().waitForProcessOutput( out, err )
			if (new File("${it}.jpg").exists()) new File("${it}.jpg").delete()
			new File("00000001.jpg").renameTo("${workspace}${it}.jpg")
		}else{
			cmd="\"${ffmpeg}\" -ss ${it*averageSeconds} -i \"${movieFile}\" -vf \"scale=415:-1\" -f image2 -t 0.001 -y ${screenshotFile}"
			cmd.execute().waitForProcessOutput( out, err )
		}
		log.debug "action=screenshot cmd=${cmd}"
		//给所有图片打上时间戳水印
		def timestamp=new GregorianCalendar(0,0,0,0,0,(it*averageSeconds as BigDecimal)/1 as int,0).time.format('HH:mm:ss')
		cmd="\"${convert}\" ${screenshotFile} -gravity SouthEast -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 2 -pointSize 16 -annotate 0 \"${timestamp}\" -stroke none -fill White -annotate 0 \"${timestamp}\" ${screenshotFile}"
		cmd.execute().waitForProcessOutput( out, err )
		def picFile=new File("${workspace}${it}.jpg")
		picsString=picsString+"${picFile.absolutePath} "
	}
	return picsString
}

//返回最后合成的文件名
def montage(picsString,movieJsonStr){
	//使用montage合并图片。title如果有中文，必须用文件的方式读取，指定font，注意，文件必须要UTF-8编码，其和字体文件可以不用指明路径，但是原始图片必须指明路径！
	log.info "action=montage"
	def tile="4x4"//列 x 行
	def font="YaHei_Monaco.ttf"
	def frame="-frame 1"
	def set=" -set label '%f\\n%2x%h'"
	def montageResult="${workspace}montage.jpg"

	def cmd="\"${montage}\" -pointsize 12 ${picsString} -background Lavender -geometry +0+0 -tile ${tile} ${frame} \"${montageResult}\""
	log.debug "action=montage cmd=${cmd}"
	log.debug "action=montage info=\"begin montage preview pic.\""
	cmd.execute().waitForProcessOutput(out,err)

	def movieJson = new JsonSlurper().parseText(movieJsonStr)
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
	log.debug "action=genTitle info${title}"

	cmd="${convert} \"${montageResult}\" -background Lavender -pointSize 18 -font ${font} label:\"${title}\" -gravity NorthWest +swap -append \"${montageResult}\""
	log.debug "action=convert cmd=${cmd}"
	log.info "action=convert info=\"add movie title info.\""
	cmd.execute().waitForProcessOutput(out,err)

	//普通文字水印
	/*
	cmd="${convert} ${montageResult} -gravity NorthEast -fill Black -pointSize 44 -draw \"text 10,10 'FFmpeg & ImageMagick'\" ${montageResult} "
	log.debug "action=AddWaterMark cmd=${cmd}"
	log.info "action=AddWaterMark info=\"convert add text warter mark.\""
	cmd.execute().waitForProcessOutput(out,err)
	*/
	//制作透明背景，带阴影的水印
	/*
	cmd="${convert} -size 520x65 xc:none -pointsize 44 -stroke black -strokewidth 2 -annotate +5+50 \"FFmpeg & ImageMagick\" -blur 0x6 -fill White -stroke Black -annotate +0+45 \"FFmpeg & ImageMagick\" watermark_shadow.png"
	log.info "action=GenWaterMark cmd=\"${cmd}.\""
	cmd.execute().waitForProcessOutput(out,err)
	*/
	//合成水印和剧情图
	cmd="${composite} -dissolve 60 -gravity NorthEast -geometry +0+0 watermark_shadow.png ${montageResult} ${montageResult}"
	log.info "action=composite cmd=\"${cmd}.\""
	cmd.execute().waitForProcessOutput(out,err)

	//清理生成的缩略图
	def delCmd="cmd /c del ${picsString}"
	log.debug "action=delete cmd=\"${delCmd}\""
	delCmd.execute().waitForProcessOutput(out,err)
	return montageResult
}

log.info "action=end info=\"successful complete.\""
