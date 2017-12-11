package com.shohiebsense.straightidiomalearn.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.shohiebsense.straightidiomalearn.MainActivity
import java.util.regex.Pattern
import android.text.*
import com.shohiebsense.straightidiomalearn.R

import org.jetbrains.anko.bundleOf
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import android.graphics.drawable.Drawable






/**
 * Created by Shohiebsense on 12/09/2017.
 */

class AppUtil {

    companion object {

        private val SDPath = "idiomalearn"
       // private val destinationFolder = SDPath

        fun navigateToFragment(context: Context, fragmentName: String) {
            var intent = Intent(context, MainActivity::class.java)

            intent.putExtra(MainActivity.intentMessage, fragmentName)
            context.startActivity(intent)
        }

        fun makeErrorLog(error: String) {
            Log.e(ConstantsUtil.ERROR_TAG, error)
        }

        fun makeDebugLog(debug: String) {
            Log.e(ConstantsUtil.DEBUG_TAG, debug)
        }

        fun isPdfDocument(extension: String): Boolean = extension.toLowerCase().endsWith(".pdf")

        fun makeToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }


        fun getHeightOfWindow(activity: Activity): Int {
            var display = activity.windowManager.defaultDisplay
            var size = Point()
            display.getSize(size)
            return size.y

        }

        fun newSpaceInString(words: String): String {

            val re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE or Pattern.COMMENTS)
            var reMatcher = re.matcher(words)
            val sentences = words.split("(?<=[.!?])\\s* ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var stringBuilder = StringBuilder()
            for (words in sentences) {
                stringBuilder.append(words)
                stringBuilder.append("\n\n")
            }


            return stringBuilder.toString()
        }

        fun splitSentencesToWords(words: String): MutableList<String> {

            val re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE or Pattern.COMMENTS)
            var reMatcher = re.matcher(words)
            val sentences = words.split("(?<=[.!?])\\s* ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var stringBuilder = mutableListOf<String>()
            for (words in sentences) {
                stringBuilder.add(words)
            }


            return stringBuilder
        }

        fun fromHtml(word: String): Spanned {
            val result: Spanned
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(word, Html.FROM_HTML_MODE_LEGACY)
            } else {
                result = Html.fromHtml(word)
            }
            return result
        }


        fun findText2() {
            val text = "0123hello9012hello8901hello7890"
            val word = "hello"

            println(text.indexOf(word)) // prints "4"
            println(text.lastIndexOf(word)) // prints "22"

            // find all occurrences forward
            var i = -1
            while ({ i = text.indexOf(word, i + 1); i }() != -1) {
                println(i)
                i++
            }

            // find all occurrences backward
            while ({ i = text.lastIndexOf(word, i - 1); i }() != -1) {
                println(i)
                i++
            } // pr
        }


        inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>)
                = T::class.java.newInstance().apply {
            arguments = bundleOf(*params)
        }


        @Throws(IOException::class)
        private fun getAccessAssets(context: Context): InputStream {
            val assetManager = context.assets
            return assetManager.open("raw/cards.zip")
        }

        lateinit var zis: ZipInputStream

        private val BUFFER_SIZE = 1024 //1024/2048

        fun unzip(context: Context): Boolean {
            AppUtil.makeDebugLog("unzipping")
            zis = ZipInputStream(context.getResources().openRawResource(R.raw.cards))
            try {
                var count = 0

                var ze : ZipEntry? = null

                while({ze = zis.nextEntry; ze}() != null){
                    //AppUtil.makeDebugLog("unzipping "+ze?.name)
                    val file = File(context.filesDir.toString() + File.separator, ze?.name) //change thte path
                    var parentDir = file.parentFile
                    if(parentDir != null){
                        parentDir.mkdirs()
                    }

                    val buffer = ByteArray(1024)
                    var fout : OutputStream = FileOutputStream(file,false)
                    val buffout = BufferedOutputStream(fout, BUFFER_SIZE)

                    AppUtil.makeDebugLog("copying "+file.name)
                    while (zis.read(buffer).let { count = zis.read(buffer, 0, BUFFER_SIZE); count != -1 }){
                        buffout.write(buffer, 0, count)
                    }

                    buffout.flush()
                    buffout.close()
                }
            } catch (ioe: IOException) {
                //Log.e(TAG,ioe.getMessage());
                AppUtil.makeDebugLog("unzipping error "+ioe.toString())
                return false
            } finally {
                zis.close()

            }


            var sdCardRoot = File(context.cacheDir.toString() + File.separator)

            for (f in sdCardRoot.listFiles()) {
                if (f.isFile())
                // make something with the name
                    AppUtil.makeDebugLog(f.name + "  " + f.length())
            }
            AppUtil.makeDebugLog("copying finished")
            return true
        }

          fun checkAja(){

        }

        fun isExists(context: Context, imagePath: String): String? {
            val dirFiles = File(context.filesDir.toString())
            try {
                for (strFile in dirFiles.list()) {
                    AppUtil.makeDebugLog(strFile + " emm")

                    if (strFile.toLowerCase().contains(imagePath.toLowerCase())) {
                        //Log.e(TAG,strFile);
                        AppUtil.makeDebugLog(strFile + "hiii ")
                    }
                    // strFile is the file name
                }
            } catch (e: NullPointerException) {
                //Log.e(BBCardsUtils.TAG,"Image not exists");
            }

            return null
        }

        fun getCardsPath(context : Context) : File = File(context.cacheDir.toString() + File.separator)


        fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Float, width: Int, height: Int): Bitmap {
            val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val sourceWidth = bitmap.width
            val sourceHeight = bitmap.height

            val xScale = width.toFloat() / bitmap.width
            val yScale = height.toFloat() / bitmap.height
            val scale = Math.max(xScale, yScale)

            val scaledWidth = scale * sourceWidth
            val scaledHeight = scale * sourceHeight

            val left = (width - scaledWidth) / 2
            val top = (height - scaledHeight) / 2

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, width, height)
            val rectF = RectF(rect)

            val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, pixels, pixels, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, null, targetRect, paint)

            return output
        }

        fun getImageString(context: Context, imageName: String): File {
            var filepath = "file://"
            var file =  File(context.filesDir.toString() + File.separator + imageName)
            AppUtil.makeDebugLog("ukuranns "+file.length() +  "  "+file.absoluteFile)
            return file
        }


        fun getFileFromAssets(context: Context) : Drawable{
                // get input stream
                val ims = context.getAssets().open("1.png")
                // load image as Drawable
                val d = Drawable.createFromStream(ims, null)
                // set image to ImageView
                return d
        }



        val storyExample =
                "NOW this is the next tale, and it tells how the Camel got his big hump.\n" +
                        "In the beginning of years, when the world was so new and all, and the Animals were just beginning to work for Man, there was a Camel, and he lived in the middle of a Howling Desert because he did not want to work; and besides, he was a Howler himself. So he ate sticks and thorns and tamarisks and milkweed and prickles, most 'scruciating idle; and when anybody spoke to him he said 'Humph!' Just 'Humph!' and no more.\n" +
                        "Presently the Horse came to him on Monday morning, with a saddle on his back and a bit in his mouth, and said, 'Camel, O Camel, come out and trot like the rest of us.'\n" +
                        "'Humph!' said the Camel; and the Horse went away and told the Man.\n" +
                        "Presently the Dog came to him, with a stick in his mouth, and said, 'Camel, O Camel, come and fetch and carry like the rest of us.'\n" +
                        "'Humph!' said the Camel; and the Dog went away and told the Man.\n" +
                        "Presently the Ox came to him, with the yoke on his neck and said, 'Camel, O Camel, come and plough like the rest of us.'\n" +
                        "'Humph!' said the Camel; and the Ox went away and told the Man.\n" +
                        "At the end of the day the Man called the Horse and the Dog and the Ox together, and said, 'Three, O Three, I'm very sorry for you (with the world so new-and-all); but that Humph-thing in the Desert can't work, or he would have been here by now, so I am going to leave him alone, and you must work double-time to make up for it.'\n" +
                        "That made the Three very angry (with the world so new-and-all), and they held a palaver, and an indaba, and a punchayet, and a pow-wow on the edge of the Desert; and the Camel came chewing on milkweed most 'scruciating idle, and laughed at them. Then he said 'Humph!' and went away again.\n" +
                        "Presently there came along the Djinn in charge of All Deserts, rolling in a cloud of dust (Djinns always travel that way because it is Magic), and he stopped to palaver and pow-pow with the Three.\n" +
                        "'Djinn of All Deserts,' said the Horse, 'is it right for any one to be idle, with the world so new-and-all?'\n" +
                        "'Certainly not,' said the Djinn.\n" +
                        "'Well,' said the Horse, 'there's a thing in the middle of your Howling Desert (and he's a Howler himself) with a long neck and long legs, and he hasn't done a stroke of work since Monday morning. He won't trot.'\n" +
                        "'Whew!' said the Djinn, whistling, 'that's my Camel, for all the gold in Arabia! What does he say about it?'\n" +
                        "'He says \"Humph!\"' said the Dog; 'and he won't fetch and carry.'\n" +
                        "'Does he say anything else?'\n" +
                        "'Only \"Humph!\"; and he won't plough,' said the Ox.\n" +
                        "'Very good,' said the Djinn. 'I'll humph him if you will kindly wait a minute.'\n" +
                        "The Djinn rolled himself up in his dust-cloak, and took a bearing across the desert, and found the Camel most 'scruciatingly idle, looking at his own reflection in a pool of water.\n" +
                        "'My long and bubbling friend,' said the Djinn, 'what's this I hear of your doing no work, with the world so new-and-all?'\n" +
                        "'Humph!' said the Camel.\n" +
                        "The Djinn sat down, with his chin in his hand, and began to think a Great Magic, while the Camel looked at his own reflection in the pool of water.\n" +
                        "'You've given the Three extra work ever since Monday morning, all on account of your 'scruciating idleness,' said the Djinn; and he went on thinking Magics, with his chin in his hand.\n" +
                        "'Humph!' said the Camel.\n" +
                        "'I shouldn't say that again if I were you,' said the Djinn; you might say it once too often. Bubbles, I want you to work.'\n" +
                        "And the Camel said 'Humph!' again; but no sooner had he said it than he saw his back, that he was so proud of, puffing up and puffing up into a great big lolloping humph.\n" +
                        "'Do you see that?' said the Djinn. 'That's your very own humph that you've brought upon your very own self by not working. To-day is Thursday, and you've done no work since Monday, when the work began. Now you are going to work.'\n" +
                        "'How can I,' said the Camel, 'with this humph on my back?'\n" +
                        "'That's made a-purpose,' said the Djinn, 'all because you missed those three days. You will be able to work now for three days without eating, because you can live on your humph; and don't you ever say I never did anything for you. Come out of the Desert and go to the Three, and behave. Humph yourself!'\n" +
                        "And the Camel humphed himself, humph and all, and went away to join the Three. And from that day to this the Camel always wears a humph (we call it 'hump' now, not to hurt his feelings); but he has never yet caught up with the three days that he missed at the beginning of the world, and he has never yet learned how to behave."






    }




}


