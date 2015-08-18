@Grab(group='com.gmongo', module='gmongo', version='0.9.5')
import com.gmongo.GMongo
import com.mongodb.gridfs.*
import org.apache.log4j.Logger

@Singleton(lazy = true) class MongodbDao{
	def log = Logger.getLogger("MongodbDao")
	def db = new GMongo().getDB("gmongo")
	def insert(record){
		log.info "action=saveToMongodb info=\"begin to save video meta and montage,preview info to mongodb\""
		def objectId=new org.bson.types.ObjectId("55d1fa25cacaae6177c8a114")
		log.info "action=objectId info=${db.video.findOne("_id":objectId)}"
		
		log.info "action=record before info=\"${record._id}\""
		db.video.insert(record)
		log.info "action=record after info=\"${record._id}\""

		log.info "action=objectId2 info=${db.video.findOne("_id":record._id)}"

		//log.info "action=recordId info=\"${savedRecord._id}\""
		//读取binary字段，输出到文件
		/*
		def imageBytes=db.videos.findOne().montage
		def fos=new FileOutputStream(new File("D:/test2222.jpg"))
		fos.write(imageBytes)
		fos.flush()
		fos.close()
		log.info ("imageBytes:${imageBytes.size()}")
		*/

		/*
		对于大文件比较适合（因为document限制不能超过4M/16M？还是因为分布式处理？），或者海量小文件？（因为索引？但是有人说gfs会两次读取，性能也不会好。）
		//gfs存储
		def gfsPhoto =new GridFS(db, "photo")
		def gfsFile = gfsPhoto.createFile(montageFile)
		gfsFile.setFilename(montageFile.name)
		gfsFile.save()
		//gfs读取
		def newFileName ="[阳光电影www.ygdy8.com].赌侠2：上海滩赌圣.BD.720p.国粤双语中字.mkv_montage.jpg"
		def gfsPhoto =new GridFS(db, "photo");
		def imageForOutput = gfsPhoto.findOne(newFileName)
		log.info "action=loadFileFromMongodb info=\"begin to load and save to folder.\""
		imageForOutput.writeTo(new File("I:/test.jpg"))
		*/
	}
}