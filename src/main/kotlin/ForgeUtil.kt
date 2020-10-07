import evolution.Genome
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * @return Returns inputstream from Forge match as List<String>
 */
fun executeForgeMatch(genomes: List<Genome>, numOfGamesInMatch: Int): List<String>{
    val pb = ProcessBuilder("java", "-Xmx8096m", "-jar", "forge-gui-desktop-1.6.36-jar-with-dependencies.jar", "sim", "-d", "${genomes[0].name}.dck", "${genomes[1].name}.dck", "-m", "$numOfGamesInMatch", "-q")
            .directory(File("F:\\MTG_Forge\\1.6.36"))
    val p = pb.start()
    p.waitFor(60, TimeUnit.SECONDS)
    val inStream: InputStream = p.inputStream
    createLogFolder()
    val f = File("./logs/${genomes[0].name}-${genomes[1].name}.log")
    f.copyInputStreamToFile(inStream)
    return f.readLines()
}

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

    val file = File("C:\\Users\\Glasshouse\\AppData\\Roaming\\Forge\\decks\\constructed\\$name.dck")
    file.writeText(sb.toString())
}