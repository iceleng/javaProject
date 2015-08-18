public class DomainDocument{
	/*
	 * video文档包含actor list，photo list和comment list
	 * 此外，含有ffprobe输出的json作为video的meta信息，文档的创建时间和最后更新时间。创建人和最后更新人，enabled标示。
	 * 此外，要有
	*/


	def video
	def actor
	def comment
	def photo

}