import groovy.json.JsonSlurper

def ffmpeg="D:/Tools/ffmpeg-20150810-git-7a7ca3c-win32-static/bin/ffmpeg.exe"
def ffprobe="D:/Tools/ffmpeg-20150810-git-7a7ca3c-win32-static/bin/ffprobe.exe"
def convert="D:/Tools/ImageMagick-6.9.1-Q16/convert.exe"
def composite="D:/Tools/ImageMagick-6.9.1-Q16/composite.exe"
def montage="D:/Tools/ImageMagick-6.9.1-Q16/montage.exe"
def movie="D:/Books/2015/201506/Wolf.Warriors.2015.1080p.WEB-DL.x264.AAC-SeeHD.mkv"
movie="D:/Workspace/Entertainment/cartoon/Pepper Pig/1/s1 01 Muddy Puddles.avi"
movie="D:/Books/2015/201506/Wolf.Warriors.2015.1080p.WEB-DL.x264.AAC-SeeHD.mkv"
movie="D:/Workspace/Entertainment/cartoon/Ardrews/安徒生经典童话－01.丑小鸭.rmvb"
movie="D:/SupportWorks/product/其他/PRM/PRM_培训视频/2012-5-7 17-18-02Tajinder.avi"
def cmd="${ffmpeg} -ss 00:10:00 -i ${movie} -y -f image2 output.jpg "

StringWriter err1 = new StringWriter()
StringWriter out1 = new StringWriter()


def ffprobeCmd="${ffprobe} -print_format json -show_format -show_streams -i \"${movie}\" "

println ffprobeCmd

def ffprobeProc=ffprobeCmd.execute()
//ffprobeProc.waitFor()
ffprobeProc.waitForProcessOutput(out1,err1)
println ffprobeProc.exitValue()
//println out1
//println err1
/*
println "ffprobeProc err text:${ffprobeProc.err.text}"
println "ffprobeProc in text:${ffprobeProc.in.text}"
println "ffprobeProc text:${ffprobeProc.text}"
*/
def movieJson=new JsonSlurper().parseText(out1.toString())
def jsonFile=new File("D:/jsonFile.txt")
if (jsonFile.exists()) jsonFile.delete()
jsonFile<<out1.toString()
println "Streams codec_type:${movieJson.streams.codec_type}"
println "Streams width:${movieJson.streams.width}"
println "Streams height:${movieJson.streams.height}"
println "Streams avg_frame_rate:${movieJson.streams.avg_frame_rate}"
println "Streams display_aspect_ratio:${movieJson.streams.display_aspect_ratio}"
println "Streams codec_name:${movieJson.streams.codec_name}"

println "format nb_streams:${movieJson.format.nb_streams}"
println "format format_name:${movieJson.format.format_name}"
println "format duration:${movieJson.format.duration}"
println "format size:${movieJson.format.size}"
println "format bit_rate:${movieJson.format.bit_rate}"
println "format creation_time:${movieJson?.format?.tags?.creation_time}"

//在左上角输出视频相关信息
/*
12345678902234567890323456789042345678905234567890623456789072345678908234567890923456789012345678902234567890323\\n
一二三四五六七八九十二二三四五六七八九十三二三四五六七八九十四二三四五六七八九十五二三四五六七八九十六二三四五六七八九十七二三四五六七\\n
*/
def videoSize=(movieJson.format.size as BigDecimal)/1204/1024 as int
if (videoSize>1000) videoSize=" ${videoSize}"
else if (videoSize>100) videoSize=" ${videoSize}."
else if (videoSize>10)  videoSize=" ${videoSize}.0"
else (videoSize>10)  videoSize=" ${videoSize}.00"

def videoStream=movieJson.streams.find{
	it.codec_type=='video'
}

def creation_time=""
def jpgList=""
(1..16).each{
	//def cmdStr="${ffmpeg} -ss ${60*it as int} -i \"${movie}\" -vf scale=-1:240 -f image2 -y ${it}.jpg "
	def cmdStr="${ffmpeg} -ss ${60*it as int} -i \"${movie}\" -vf scale=427:-1 -f image2 -y ${it}.jpg "
	//ffmpeg截取rmvb基本会全部都弄成花屏
	/*
	-vf scale=512:-3：-vf表示视频格式，scale是缩放，512:-3表示强制将宽度设置为512，高度写为-3表示保持高宽比，也可以设置为-1或-2，具体表示什么，有兴趣的可以尝试一下。如果要强制转化为统一大小，可以直接写-vf scale=640:480，但笔者个人建议用-3来保持高宽比。-vf里还有expand和crop参数，例如：-vf scale=512:384,expand=512:384:::1,crop=512:384:0:0，expand表示膨胀，crop表示裁剪；
	*/
	cmdStr="mplayer.exe \"${movie}\" -ss ${60*it as int} -noframedrop -nosound -vf scale=427:-3 -vo jpeg -frames 1 "
	println cmdStr
	def proc=cmdStr.execute()
	proc.waitForProcessOutput(out1,err1)
	if (cmdStr.contains("mplayer.exe") && new File("${it}.jpg").exists()) new File("${it}.jpg").delete()
	new File("00000001.jpg").renameTo("${it}.jpg")

	cmd2="${convert} ${it}.jpg -gravity NorthWest -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 2 -pointSize 16 -annotate 0 \"01:23:45\" -stroke none -fill White -annotate 0 \"01:23:45\" -append ${it}.jpg".execute().waitForProcessOutput(out1,err1)
	jpgList=jpgList+"${it}.jpg "
}
def cmd1="${montage} ${jpgList} -background White -geometry +0+0 -gravity North -tile 4x4 output.jpg"
println cmd1
cmd1.execute().waitForProcessOutput(out1,err1)


def titleInfo="""
视频名称：${movieJson.format.filename}\\n
视屏大小：${videoSize} MB　　分辨率：${videoStream.width} x ${videoStream.height}　　长宽比：${videoStream.display_aspect_ratio}　　帧/秒：${Eval.me(videoStream.avg_frame_rate)}　　流数：${movieJson.format.nb_streams}　　码率：${(movieJson.format.bit_rate as long)/1024 as int} kb\\n
播放时长：${new GregorianCalendar(0,0,0,0,0,(movieJson.format.duration as BigDecimal)/1 as int,0).time.format('HH:mm:ss')}　　创建时间：${movieJson?.format?.tags?.creation_time} 　　视频编码：${videoStream.codec_name}　　封装格式：${movieJson.format.format_name}  
""".toString()
cmd="${convert} output.jpg -background lavender -font YaHei.Consolas.1.12.ttf -pointSize 24 label:\"${titleInfo}\" -gravity NorthWest +swap -append output_title.jpg".execute().waitForProcessOutput(out1,err1)

//文字水印
cmd="${convert} output_title.jpg -gravity NorthEast -fill Black -pointSize 40 -draw \"text 15,25 'FFmpeg & ImageMagick'\" output_title.jpg ".execute().waitForProcessOutput(out1,err1)
//png的图片可以背景透明
cmd="${convert} -background none -fill white -font YaHei.Consolas.1.12.ttf -pointsize 48 -stroke black -strokewidth 2 -gravity center -rotate 360 label:\"这是ice制作的文字水印\" waterMark.png".execute().waitForProcessOutput(out1,err1)

//制作带阴影的水印
cmd="${convert} -size 520x65 xc:none -font YaHei.Consolas.1.12.ttf -pointsize 48 -stroke black -strokewidth 2 -annotate +5+50 \"这是ice制作的文字水印\" -blur 0x6 -fill White -stroke Black -annotate +0+45 \"这是ice制作的文字水印\" watermark_shadow.png"
println cmd
cmd.execute().waitForProcessOutput(out1,err1)

//合成透明水印！cool
cmd="${composite} -dissolve 60 -gravity center -geometry +0+60 watermark_shadow.png output_title.jpg output_title_watermark60.jpg".execute().waitForProcessOutput(out1,err1)

cmd="${composite} -gravity center -geometry +0+60 watermark_shadow.png output_title.jpg output_title_watermark.jpg".execute().waitForProcessOutput(out1,err1)

//cmd="${convert} -size 480x80 xc:lavender -pointsize 44 -annotate +5+50 \"FFmpeg & ImageMagick\" -blur 0x6 -fill White -stroke Black -annotate +0+45 \"FFmpeg & ImageMagick\" font_shadow_fuzzy.jpg".execute().waitForProcessOutput(out1,err1)

//图片水印
//cmd="${convert} out_anno.jpg font_shadow_fuzzy.jpg -gravity NorthEast -geometry +5+10 -composite out_anno3.jpg ".execute().waitForProcessOutput(out1,err1)
//在指定位置上输出白色的文字，并且用灰色背景框框住。
//"${convert} -fill white -undercolor \"#888888\" -gravity NorthWest -fill White -pointSize 16 -draw \"text 350,300 '01:23:45'\" out.png output.jpg".execute().waitForProcessOutput(out,err)

//montage 用title加白顶
//"${montage} out.png -background White -geometry +0+0 -gravity North -tile 1x1 -title \" \" output.jpg".execute().waitForProcessOutput(out,err)

//outline，黑边白字 output.jpg, out.png
//"${convert} out.png -gravity NorthWest -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 2 -pointSize 16 -annotate 0 \"01:23:45\" -stroke none -fill White -annotate 0 \"01:23:45\" -append output.jpg".execute().waitForProcessOutput(out,err)

//这种程序不会退出……
//"\"mspaint.exe\" d:/output.jpg".execute().waitForProcessOutput(out,err)

//println cmd
//def proc=cmd.execute()

//def proc=ffmpeg.execute()
//高端的用法啊，第一次见
//proc.consumeProcessOutput( System.out, System.err)
//proc.consumeProcessOutput(out,err)

//国内网站上用如下方法输出直接结果
/*
def inputStream=proc.getInputStream();        
while(inputStream.read() != -1) {      
	System.out.println(inputStream.read());      
}      
inputStream.close();      
*/

//有输出，但是依然会block，本来是可以的，但是ffmpeg忘记加 -y参数，导致文件覆盖时等到用户输入确认，所以才hang住。
//proc.waitForProcessOutput(System.out, System.err)
//无输出，block，应该err的输出。其实也是可用的，block住和上面的原因一样。
//proc.waitForProcessOutput(out,err)
//proc.waitFor()
//清空is流，依然无效，还是会block

/*
BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
while ((reader.readLine()) != null) {}

BufferedReader readerErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
while ((readerErr.readLine()) != null) {}

proc.waitFor()
*/

/*
ProcessBuilder pb = new ProcessBuilder("tasklist");
pb.redirectErrorStream(true);
Process process = pb.start();
BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
String line;
while ((line = reader.readLine()) != null)
    System.out.println("tasklist: " + line);
process.waitFor();
*/
/*
def jpgList=""
(1..16).each{
	def cmdStr="${ffmpeg} -ss ${300*it as int} -i \"${movie}\" -vf scale=-1:240 -f image2 -y ${it}.jpg "
	println cmdStr
	def proc=cmdStr.execute()
	proc.waitForProcessOutput(out1,err1)
	//proc.consumeProcessOutput(out,err)
	//proc.waitFor()
cmd2="${convert} ${it}.jpg -gravity SouthEast -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 2 -pointSize 16 -annotate 0 \"01:23:45\" -stroke none -fill White -annotate 0 \"01:23:45\" ${it}.jpg"

//cmd2="${convert} ${it}.jpg -gravity Center -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 2 -pointSize 16 -annotate 0 \"01:23:45\" -background none -shadow 100x3+0+0 +repage -stroke none -fill White -annotate 0 \"01:23:45\" +swap -gravity South -geometry +0-3 -composite ${it}.jpg"

//cmd2="${convert} ${it}.jpg xc:lightblue -font YaHei.Consolas.1.12.ttf -stroke Black -strokeWidth 8 -pointSize 16 -annotate +5+7 \"01:23:45\" -stroke none -fill White -annotate +5+7 \"01:23:45\" -gravity South ${it}.jpg"

println cmd2
cmd2.execute().waitForProcessOutput(out1,err1)	
	jpgList=jpgList+"${it}.jpg "
}
cmd1=
"${montage} ${jpgList} -background White -geometry +0+0 -gravity North -tile 4x4 -title \"Title \" output.jpg"
println cmd1
cmd1.execute().waitForProcessOutput(out1,err1)
*/


//利用ffmpeg自带的截图并组合功能，效率太差，花费了近11分钟才完成，感觉还是家里的台式机效率高些，对于5G的mkv文件也就4分半完成截图。
/*
println new Date()
"${ffmpeg} -ss 00:00:10 -i ${movie} -frames 1 -vf \"select=not(mod(n\\,7500)),scale=-1:240,tile=4x4\" out.png".execute().waitForProcessOutput(out,err)
println new Date()
*/
