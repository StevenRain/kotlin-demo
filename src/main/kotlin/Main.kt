import net.coobird.thumbnailator.Thumbnails
import java.awt.image.BufferedImage

class Origin {
    var originX: Int = 0
    var originY: Int = 0
}

class Center {
    var centerX: Int = 0
    var centerY: Int = 0
}

class PlateNumberData {
    var width: Int = 0
    var height: Int = 0
    var origin: Origin? = null
    lateinit var center: Center
}

fun getPlateNumberInfo(bufferedImage: BufferedImage): PlateNumberData {
    val widthOfImage = bufferedImage.width
    val heightOfImage = bufferedImage.height
    val minX = bufferedImage.minX
    val minY = bufferedImage.minY
    var redValue: Int
    var greenValue: Int
    var blueValue: Int
    var widthOfPlateNumber = 0
    var heightOfPlateNumber = 0
    var originOfPlateNumber: Origin? = null
    for(x in minX until  widthOfImage) {
        for(y in minY until heightOfImage) {
            val pixel:Int = bufferedImage.getRGB(x, y)
            redValue = (pixel and 0xFF0000).shr(16)
            greenValue = (pixel and 0xFF00).shr(8)
            blueValue = (pixel and 0xFF)

            if(redValue < 100 && greenValue < 100 && blueValue > 100) {
                if(originOfPlateNumber == null) {
                    originOfPlateNumber = Origin().apply {
                        originX = x
                        originY = y
                    }
                }
                widthOfPlateNumber = x
                heightOfPlateNumber = y
            }
        }
    }

    return PlateNumberData().apply {
        width = widthOfPlateNumber
        height = heightOfPlateNumber
        origin = originOfPlateNumber
        center = Center().apply {
            centerX = (width - origin!!.originX) / 2
            centerY = (height - origin!!.originY) / 2
        }
    }
}

fun writePlateNumber(plateNumber: String) {
    val fileName = "demo.jpg"
    val outFileName = fileName.substringBefore(".").plus("_out.").plus(fileName.substringAfter("."))
    println(outFileName)
    val bufferedImage: BufferedImage = Thumbnails.of(fileName).scale(1.0).asBufferedImage()
    val plateNumberData = getPlateNumberInfo(bufferedImage)

    if(plateNumber.length < 2) {
        return
    }
    val chinesePlateNumber = plateNumber.substring(0, 2).plus("·").plus(plateNumber.substring(2, plateNumber.length))
    val totalCharWidth = (plateNumberData.width * 0.9 / chinesePlateNumber.length)
    val totalCharHeight = (plateNumberData.height * 0.75)
    val charWidth = (totalCharHeight / 2).toInt() //高为宽的两倍




//    Thumbnails.of(fileName)
//            .size(1280,1024)
//            .watermark(Positions.CENTER,ImageIO.read(newFile("images/watermark.png")),0.5f)
//            .watermark()
//            .outputQuality(0.8f)
//            .toFile(outFileName)
}

fun main(args: Array<String>) {
    val fileName = "demo.jpg"
    val bufferedImage: BufferedImage = Thumbnails.of(fileName).scale(1.0).asBufferedImage()
//    val plateNumberData = getPlateNumberInfo(bufferedImage)
//    println("${plateNumberData.width}, ${plateNumberData.height}, " +
//            "${plateNumberData.origin!!.originX}, ${plateNumberData.origin!!.originY}, " +
//            "${plateNumberData.center.centerX}, ${plateNumberData.center.centerY}")

    writePlateNumber("苏AL60R7")
}