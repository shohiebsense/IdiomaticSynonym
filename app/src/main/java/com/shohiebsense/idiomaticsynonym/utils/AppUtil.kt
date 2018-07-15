package com.shohiebsense.idiomaticsynonym.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.Toast
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.SplashActivity
import com.shohiebsense.idiomaticsynonym.UnderliningActivity
import edu.stanford.nlp.ling.Sentence
import org.jetbrains.anko.bundleOf
import java.io.*
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import edu.stanford.nlp.tagger.maxent.MaxentTagger
import org.apache.commons.lang3.time.StopWatch
import org.jetbrains.anko.defaultSharedPreferences
import android.support.design.widget.Snackbar




/**
 * Created by Shohiebsense on 12/09/2017.
 */

class AppUtil {

    companion object {

        private val SDPath = "idiomalearn"
        val PREF_TEXTS = "english"
        val PREF_PRE_DATA = "data"
        val PREF_MAIN_GUIDANCE = "guidance"
        val PREF_IDIOM_GUIDANCE = "idiomGuidance"
        val PREF_IS_FILE_UPLOADED_EVENT = "isfileuploadd"
        val PREF_FILE_UPLOADED_NAME = "fileUploadedName"

        fun setFileUploadedNameEvent(context: Context, fileName : String){
            context.defaultSharedPreferences.edit().putString(PREF_FILE_UPLOADED_NAME, fileName).apply()
        }

        fun getFileUploadedNameEvent(context: Context) : String{
            val isAsked =  context.defaultSharedPreferences.getString(PREF_FILE_UPLOADED_NAME,"")
            return isAsked
        }

        fun setFileUploadedEvent(context: Context, isAsked : Boolean){
            context.defaultSharedPreferences.edit().putBoolean(PREF_IS_FILE_UPLOADED_EVENT, isAsked).apply()
        }

        fun isFileUploadedEvent(context: Context) : Boolean{
            val isAsked =  context.defaultSharedPreferences.getBoolean(PREF_IS_FILE_UPLOADED_EVENT,false)
            return isAsked
        }

        fun setIdiomGuidance(context: Context, isAsked : Boolean){
            context.defaultSharedPreferences.edit().putBoolean(PREF_IDIOM_GUIDANCE, isAsked).apply()
        }

        fun getIdiomGuidance(context: Context) : Boolean{
            val isAsked =  context.defaultSharedPreferences.getBoolean(PREF_IDIOM_GUIDANCE,true)
            return isAsked
        }


        fun setMainGuidance(context: Context, isAsked : Boolean){
            context.defaultSharedPreferences.edit().putBoolean(PREF_MAIN_GUIDANCE, isAsked).apply()
        }

        fun getMainGuidance(context: Context) : Boolean{
            val isAsked =  context.defaultSharedPreferences.getBoolean(PREF_MAIN_GUIDANCE,true)
            return isAsked
        }


        fun setPreDataAskingPreference(context: Context, isAsked : Boolean){
            context.defaultSharedPreferences.edit().putBoolean(PREF_PRE_DATA, isAsked).apply()
        }

        fun getPreDataAskingPreference(context: Context) : Boolean{
            val isAsked =  context.defaultSharedPreferences.getBoolean(PREF_PRE_DATA,false)
            return isAsked
        }


        fun navigateToFragment(context: Context, fragmentName: String) {
            var intent = Intent(context, UnderliningActivity::class.java)

            intent.putExtra(UnderliningActivity.INTENT_MESSAGE, fragmentName)
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

        fun newSpaceBetweenSentences(words: String): String {

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

      /*  fun splitParagraphsIntoSentences(words: String): List<String> {
            val re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE or Pattern.COMMENTS)
            var reMatcher = re.matcher(words)
            val sentences = words.split("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$) ".toRegex()).dropLastWhile { it.isEmpty() }
            return sentences
        }
*/
        fun putStringToPreferences(context: Context, words: String){
            AppUtil.makeDebugLog("has been put "+words)
            context.defaultSharedPreferences.edit().putString(PREF_TEXTS, words).apply()
        }

        fun getTextPreferences(context: Context) : String{
            val word =  context.defaultSharedPreferences.getString(PREF_TEXTS,"")
            AppUtil.makeDebugLog("worddss "+word)
            return word
        }

        fun splitParagraphsIntoSentences(words: String): MutableList<String> {
            val tokenizedSentences = MaxentTagger.tokenizeText(StringReader(words))
            val timer = StopWatch()
            timer.start()

            val sentences = arrayListOf<String>()
            for (act in tokenizedSentences)
            //Travel trough sentences
            {
                 //This is your sentence
                sentences.add(Sentence.listToString(act))
            }
            timer.stop()
            val seconds = timer.time/60
            AppUtil.makeErrorLog("time elapsed during tokenize "+seconds+" seconds "+sentences.size)

            return sentences
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

        fun toHtml(charSequence: CharSequence) : String{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return Html.toHtml(charSequence as Spanned?, Html.FROM_HTML_MODE_LEGACY)
            }
            return Html.toHtml(charSequence as Spanned?)
        }

       /* fun toHtml(context: Context, charSequence: CharSequence) : String{
            return HtmlCompat.toHtml(context, charSequence as Spanned?, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)

        }*/


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

                   // AppUtil.makeDebugLog("copying "+file.name)
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


        val translateExample = "Sekarang ini adalah cerita berikutnya, dan ini menceritakan bagaimana Unta mendapat punuk besarnya.\n" +
                "\"Di awal tahun, saat dunia jadi baru dan semua, dan hewan-hewan baru saja mulai bekerja untuk Manusia, ada Unta, dan dia tinggal di tengah Gurun Tipik karena dia tidak ingin bekerja; Dan selain itu, dia adalah seorang Howler sendiri, jadi dia makan tongkat dan duri, tamaris dan milkweed dan tusukan, paling 'menyiksa menganggur; dan saat ada yang berbicara dengannya dia mengatakan' Humph! 'hanya' Humph! ' dan tidak lebih.\n" +
                "\"Kuda itu datang pada hari Senin pagi, dengan pelana di punggungnya dan sedikit di mulutnya, dan kata, 'Unta, untalah, keluar dan berlarilah seperti kita semua.'\n" +
                "\"'Humph!' kata si Unta, dan Kuda pergi dan memberi tahu Manusia.\n" +
                "\"Anjing itu datanglah, dengan tongkat di mulutnya, dan kata, 'Unta, unta, datang dan ambil dan bawa seperti kita semua.'\n" +
                "\"'Humph!' kata si Unta, dan Anjing itu pergi dan memberi tahu Manusia.\n" +
                "\"Saat ini Sapi datang penuh, dengan kuk di lehernya dan berkata, 'Unta, Unta, datang dan bajak seperti kita semua.'\n" +
                "\"'Humph!' kata si Unta, dan si Sapi pergi dan memberi tahu Sang Manusia.\n" +
                "\"Di penghujung hari, Manusia memanggil Kuda dan Anjing dan Sapi bersama-sama, dan kata, 'Tiga, Wahai Tiga, saya sangat menyesal untuk Anda (dengan dunia yang baru dan baru saja), tapi itu Humph-hal di Gurun tidak bisa bekerja , atau dia pasti sudah berada di sini sekarang, jadi saya akan meninggalkannya sendirian, dan kamu harus bekerja dua kali untuk menebusnya. '\n" +
                "\"Itu membuat Tiga sangat marah (dengan dunia yang sangat baru dan baru), dan mereka memegang palet, dan sebuah pow-wow di tepi Gurun, dan unta datang mengunyah. Pada milkweed yang paling 'menyiksa keberur , dan menertawakan mereka. Lalu dia bilang 'Humph!' dan pergi lagi.\n" +
                "\"Saat ini datanglah Djinn yang bertanggung jawab atas semua gurun pasir, berguling di awan debu (Djin selalu berjalan seperti itu karena Magic), dan dia berhenti untuk palet dan pow-pow with the Three.\n" +
                "\"Djinn of All Deserts,\" kata si Kuda, 'apakah benar ada orang yang menganggur, dengan dunia yang baru dan baru?'\n" +
                "\"'Tentu saja tidak,' kata Djinn.\n" +
                "\"'Baiklah,' kata Kuda, 'ada sesuatu di tengah Gurun Perahu Anda dan dia yang sedang mengerjakannya, jangan pernah berangkat kerja pagi.'\n" +
                "\"Wah!\" kata Djinn, bersiul, 'itu unta ku, untuk semua emas di Arabia! Apa yang dia katakan tentang itu?'\n" +
                "\"'Dia bilang \\\" Humph! \\ '' Kata si Anjing; ' dan dia tidak akan mengambil dan membawa. '\n" +
                "\"'Apakah dia mengatakan hal lain?'\n" +
                "\"'Hanya \\\" Humph! \\ \"; Dan dia tidak akan membajak, 'kata si Sapi.\n" +
                "\"'Bagus sekali,' kata Djinn. 'Aku akan melenturkannya jika kamu mau menunggu sebentar.'\n" +
                "\"Djinn menggulung dirinya dalam novel debu, dan bawa suatu bantalan penuh padang pasir, dan temukan unta yang paling 'menganggur dengan susah payah, lihat bayangannya sendiri di genangan air.\n" +
                "'' Temanku yang panjang dan menggelegak, 'kata Djinn,' apa yang kudengar tentang perbuatanmu ini, dengan dunia yang baru dan baru? '\n" +
                "\"'Humph!' kata Unta\n" +
                "\"Djinn duduk, dengan dagunya di bangun, dan mulai berpikir Sihir Besar, sementara Unta melihat bayangannya sendiri di genangan udara.\n" +
                "'' Anda telah memberi Tiga pekerjaan tambahan sejak Senin pagi, semua karena kelalaian Anda yang 'menyiksa', kata Djinn, dan dia terus memikirkan Magics, dengan dagunya ada di sus.\n" +
                "\"'Humph!' kata Unta\n" +
                "'' Saya tidak bisa mengatakannya lagi kalau saya jadi Anda, 'kata Djinn, Anda mungkin sering mengatakannya. Gelembung, saya ingin Anda bekerja.'\n" +
                "\"Dan Unta mengatakan 'Humph!' Sekali lagi, tapi tidak lama kemudian dia mengatakannya tentang melihat punggungnya, dia sangat suka, terengah-engah dan terengah-engah ke dalam kelembutan besar yang besar.\n" +
                "\"'Apa kamu melihat itu?' kata Djinn, \"Itu kelembutanmu sendiri yang kamu bawa ke atas diri sendiri dengan tidak bekerja. Hari ini adalah hari Kamis, dan kamu tidak melakukan pekerjaan sejak hari Senin, saat dimulai. Sekarang kamu akan pergi bekerja. '\n" +
                "\"Bagaimana saya bisa,\" kata Unta, 'dengan kelembutan ini di punggung saya?'\n" +
                "\"'Itu itu tujuan,' kata Djinn, 'semua karena Anda enam hari"




        val translatedIdioms = "karena,jika saya di posisi kamu, masak, sedikit, agak, mengisi, mengejar, bangga atas, maaf atas, menyesal," +
                "seperti itu, berwenang, keluar dari, muncul, terbit, tahu tentang, dengar tentang, mampu"


       /* fun getSentence(text: String, word: String): String {
            val END_OF_SENTENCE = Pattern.compile("\\.\\s+")

            val lcword = word.toLowerCase()
            return END_OF_SENTENCE.splitAsStream(text)
                    .filter({ s -> s.toLowerCase().contains(lcword)

                    })
                    .findAny()
                    .orElse(null)
        }*/


        fun getListOfIdioms(idioms : String) : List<String>{

            var result: List<String> = idioms.split(",").mapIndexed { index, it ->

                it.trim()

            }
            //result.forEach { println(it) }

            return result
        }

        fun getListOfIndexedSentences(indexedSentences : String) : List<String>{
            return getListOfIdioms(indexedSentences)
        }

        fun separateParagraphIntoEachLine(text: String, indexedSentences: String) : String {
            val tokenizedSentences = MaxentTagger.tokenizeText(StringReader(text))
            var indexedSentenceNumbers = getListOfIdioms(indexedSentences)
            var sentences = ""
            for (i in tokenizedSentences.indices)
            //Travel trough sentences
            {
                var sentence = Sentence.listToString(tokenizedSentences[i])
                if(indexedSentenceNumbers.contains(i.toString())){
                    sentence.prependIndent("<b>")
                    sentence += "</b>"
                }
                else{
                    AppUtil.makeDebugLog("falsee")
                }
                sentences += sentence + "<br/>"
            }
            return sentences
        }


        fun checkInternetConnection(context : Context) : Boolean{
            val ConnectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = ConnectionManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


        fun dp2Px(context: Context?, dp: Float): Int {
            if (context != null) {
                val scale = context.resources.displayMetrics.density
                return (dp * scale + 0.5f).toInt()
            }
            return 2
        }

        fun getOnlyFileName(name : String) : String{
            return name.substring(name.lastIndexOf("/")+1)
        }


        fun restartApplication(oontext : Context) {
            val i = Intent(oontext, SplashActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            oontext.startActivity(i)
        }

     /*   fun showSnackbar(message: String, root: View) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
        }*/

        fun fileExt(url: String): String? {
            var url = url
            if (url.indexOf("?") > -1) {
                url = url.substring(0, url.indexOf("?"))
            }
            if (url.lastIndexOf(".") == -1) {
                return null
            } else {
                var ext = url.substring(url.lastIndexOf(".") + 1)
                if (ext.indexOf("%") > -1) {
                    ext = ext.substring(0, ext.indexOf("%"))
                }
                if (ext.indexOf("/") > -1) {
                    ext = ext.substring(0, ext.indexOf("/"))
                }
                return ext.toLowerCase()

            }
        }

         val END_OF_SENTENCE = Pattern.compile("\\.\\s+")
        fun getSentence(text: String, word: String):  ArrayList<String> {
            var index = 0
            var endIndex = 0
            var foundedSentences = arrayListOf<String>()
            val lcword = word.toLowerCase()
            for (sentence in END_OF_SENTENCE.split(text)) {
                if (sentence.toLowerCase().contains(lcword)) {
                    //get index
                    index = lcword.indexOf(sentence)
                    endIndex = index + sentence.length - 1
                    foundedSentences.add(sentence)
                }
            }
            return foundedSentences
        }

    }





}


