//==是否测试模式 	"encoding":"cp936",
testFlag=true
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
movieExt="avi|mpeg|mp4|flv|rm|rmvb|mkv|wmv"
movieDir="J:/backup/hmovie/201405"
movieFile="J:/backup/done/[阳光电影www.ygdy8.com].赌侠2：上海滩赌圣.BD.720p.国粤双语中字.mkv"
//初始化的screenConfig
screenConfig=[
	titleFlag:true,								//在montage的图上是否添加video meta
	watermarkFlag:true,							//在montage的图上是否添加水印
	workspace:"./output/",						//临时图片输出路径
	videoJson:null,								//video meta信息（json格式）
	screenNumber:12,							//视频截图数量
	tile:"4x3",									//montage布局 4x4
	screenWidth:416,							//视频截图宽度
	ffmpeg:ffmpeg,					
	convert:convert,
	composite:composite,
	montage:montage,
	mplayer:mplayer,
	timestampGravity:"SouthEast",				//时间戳水印位置
	timestampFont:"YaHei_Monaco.ttf",			//加文字时指定字体，避免中文乱码
	timestampPointSize:16,
	timestampStrokeWidth:2,						//时间戳边框宽度
	timestampStrokeColor:"Black",				//时间戳边框颜色
	timestampStrokeFill:"White",				//时间戳填充颜色
	montageFrame:1,								//合并图片边框
	montageBackground:"Lavender",				//合并图片指定背景
	watermarkFont:"YaHei_Monaco.ttf",			//title指定字体，避免中文乱码
	watermarkBackground:"White",				//title背景，Lavender
	watermarkGravity:"NorthWest",				//title位置
	watermarkPointSize:18 						//title字体大小
]