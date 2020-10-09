# mtg-deck-runner

## Setup
Creat a `config.yaml` file in `src/main/resources` with the following:  
`generations` - Number of generations for genome breeding  
`elitism` - When building next generation, keep the best genome and do not breed it  
`populationSize` - Number of genomes in a generation  
`swapChance` - Chance for 2 genomes to swap halves   
`newChild` - If you should introduce a "new random child" to the generation  
`deckOutputFilePath` - Path to forge constructed decks ex. "..AppData\\Roaming\\Forge\\decks\\constructed\\"  
`forgeDir` - Location of the forge directory with jar file  
`forgeJar` - Name of jar file (include .jar)  
