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
    for(x in minX..widthOfImage) {
        for(y in minY..heightOfImage) {
            val pixel:Int = bufferedImage.getRGB(minX, 0)
            redValue = (pixel and 0xFF0000).shr(16)
            greenValue = (pixel and 0xFF00).shr(8)
            blueValue = (pixel and 0xFF)

            if(redValue < 50 && greenValue < 50 && blueValue > 100) {
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
    val fileName = "bluedemo.jpg"
    val outFileName = fileName.sub
    val bufferedImage: BufferedImage = Thumbnails.of(fileName).scale(1.0).asBufferedImage()
    val plateNumberData = getPlateNumberInfo(bufferedImage)

    if(plateNumber.length < 2) {
        return
    }
    val chinesePlateNumber = plateNumber.substring(0, 2).plus("·").plus(plateNumber.substring(2, plateNumber.length))
    val charWidth = plateNumberData.width * 0.9 / chinesePlateNumber.length
    val charHeight = plateNumberData.height * 0.75
}

fun main(args: Array<String>) {
    val fileName = "bluedemo.jpg"
    val bufferedImage: BufferedImage = Thumbnails.of(fileName).scale(1.0).asBufferedImage()
    val plateNumberData = getPlateNumberInfo(bufferedImage)
    println("${plateNumberData.width}, ${plateNumberData.height}, " +
            "${plateNumberData.origin!!.originX}, ${plateNumberData.origin!!.originY}, " +
            "${plateNumberData.center.centerX}, ${plateNumberData.center.centerY}")

    writePlateNumber("苏AL60R7")
}