import net.coobird.thumbnailator.Thumbnails
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.Font.PLAIN
import java.io.FileOutputStream
import javax.imageio.ImageIO


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

/**
 * 根据色调判断颜色是否一样
 * */
fun isPixelBlue(color2: Color): Boolean {
    val color1 = Color.BLUE
    val color1Floats = FloatArray(3)
    Color.RGBtoHSB(color1.red, color1.green, color1.blue, color1Floats)

    val color2Floats = FloatArray(3)
    Color.RGBtoHSB(color2.red, color2.green, color2.blue, color2Floats)

    val colorDifference = Math.abs((color1Floats[0] - color2Floats[0]).toDouble())
    if(colorDifference < 0.01 && color2.red < 100 && color2.green < 100 && color2.blue > 200) {
        return true
    }
    return false
}

fun getPlateNumberInfo(bufferedImage: BufferedImage): PlateNumberData {
    val widthOfImage = bufferedImage.width
    val heightOfImage = bufferedImage.height
    val minX = bufferedImage.minX
    val minY = bufferedImage.minY
    var widthOfPlateNumber = 0
    var heightOfPlateNumber = 0
    var originOfPlateNumber: Origin? = null
    println("图片宽度 ${widthOfImage}px, 图片高度 ${heightOfImage}px")
    for(x in minX until  widthOfImage) {
        for(y in minY until heightOfImage) {
            val pixel:Int = bufferedImage.getRGB(x, y)
            val color = Color(pixel)

            if(isPixelBlue(color)) {
                if(originOfPlateNumber == null) {
                    println("color = $color, x = $x, y = $y")
                    originOfPlateNumber = Origin().apply {
                        originX = x
                        originY = y
                    }
                }else {
                    widthOfPlateNumber = x - originOfPlateNumber.originX
                    heightOfPlateNumber = y - originOfPlateNumber.originY
                }
            }
        }
    }

    return PlateNumberData().apply {
        width = widthOfPlateNumber
        height = heightOfPlateNumber
        origin = originOfPlateNumber
        center = Center().apply {
            centerX = origin!!.originX + width / 2
            centerY = origin!!.originY + height / 2
        }
    }
}

fun writePlateNumber(plateNumber: String) {
    val fileName = "audi.jpg"
    val outFileName = fileName.substringBefore(".").plus("_out.").plus(fileName.substringAfter("."))
    val bufferedImage: BufferedImage = Thumbnails.of(fileName).scale(1.0).asBufferedImage()
    val plateNumberData = getPlateNumberInfo(bufferedImage)

    println("源文件名 $fileName, 输出文件名 $outFileName")
    println("车牌原点 X = ${plateNumberData.origin!!.originX}, Y = ${plateNumberData.origin!!.originY}, " +
            "车牌大小 ${plateNumberData.width}px X ${plateNumberData.height}px, " +
            "车牌中心 X = ${plateNumberData.center.centerX}, Y = ${plateNumberData.center.centerY}")

    if(plateNumber.length < 2) {
        return
    }
    val chinesePlateNumber = plateNumber.substring(0, 2).plus("·").plus(plateNumber.substring(2, plateNumber.length))
    val totalCharWidth = plateNumberData.width * 0.8
    val totalCharHeight = (plateNumberData.height * 0.75)
    val charWidth = (totalCharHeight / 2).toInt() //高为宽的两倍
    val charHeight = totalCharHeight.toInt()
    val fontSize = (charHeight * 0.8).toInt()
    println("字符宽度 ${charWidth}px, 字长高度 ${charHeight}px")

    val graphics2D = bufferedImage.graphics
    graphics2D.color = Color.WHITE
    graphics2D.font = Font("黑体", PLAIN, fontSize)
    val startXOfPlateNumber = (plateNumberData.origin!!.originX + (plateNumberData.width - totalCharWidth) / 2).toInt()
    val startYOfPlateNumber = plateNumberData.origin!!.originY + plateNumberData.height / 2 + charHeight / 4
    graphics2D.drawString(chinesePlateNumber, startXOfPlateNumber, startYOfPlateNumber)

    graphics2D.color = Color.RED
    graphics2D.drawString(".", plateNumberData.origin!!.originX, plateNumberData.origin!!.originY)
    graphics2D.drawString(".", plateNumberData.center.centerX, plateNumberData.center.centerY)
    ImageIO.write(bufferedImage, "JPG", FileOutputStream(outFileName))
}

fun main(args: Array<String>) {
    writePlateNumber("苏AL60R7")
}