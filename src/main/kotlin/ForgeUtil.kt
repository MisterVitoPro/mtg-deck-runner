import com.sun.xml.internal.fastinfoset.util.StringArray
import evolution.Genome
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * @return Returns InputStream from Forge match as List<String>
 */
fun executeForgeMatch(deckName1: String, deckName2: String, numOfGamesInMatch: Int, quiet: Boolean = true): List<String>{
    // Create Arg array
    val args = mutableListOf<String>()
    args.add("java")
    args.add("-Xmx8096m")
    args.add("-jar")
    args.add("forge-gui-desktop-1.6.37-SNAPSHOT-jar-with-dependencies.jar")
    args.add("sim")
    args.add("-d")
    args.add("$deckName1.dck")
    args.add("$deckName2.dck")
    args.add("-m")
    args.add("$numOfGamesInMatch")
    if(quiet) args.add("-q")

    // Execute Simulation
    val pb = ProcessBuilder(args).directory(File(configs.forgeDir))
    val p = pb.start()
    p.waitFor(60, TimeUnit.SECONDS)

    // Take InputStream, put into file then return as string
    val inStream: InputStream = p.inputStream
    createLogFolder()
    val f = File("./logs/${deckName1}-${deckName2}.log")
    f.copyInputStreamToFile(inStream)
    return f.readLines()
}

/**
 * Create 'logs' directory in root
 */
fun createLogFolder(){
    val f = File("./logs")
    if(!f.exists()){
        f.mkdirs()
        println("Created 'logs' directory")
    }
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
}

fun getLastMatchWinInt(fLogMatchLine: String, deck: String): Int{
    val matchWinParse: String = fLogMatchLine.substring(fLogMatchLine.lastIndexOf(deck) + deck.length + 2)[0].toString()
    return matchWinParse.toInt()
}

fun forgeOutput(genome: Genome){
    forgeOutput(genome.library.cards, genome.name)
}

fun forgeOutput(list: MutableList<Card>, name: String){
    val sb = StringBuilder()
    sb.append("[metadata]\n")
    sb.append("Name=$name\n")
    sb.append("[Main]\n")
    val frequenciesByFirstChar = list.groupingBy { it.name }.eachCount()
    frequenciesByFirstChar.toSortedMap()
            .forEach { (name, n) ->
                sb.append("$n $name\n")
            }

    // TODO Create a default location
    val file = File("${configs.deckOutputFilePath}\\$name.dck")
    file.writeText(sb.toString())
}