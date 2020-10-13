# mtg-deck-runner

## Setup
Creat a `config.yaml` file in `src/main/resources` with the following: 
`format` - Set the format you want to use legality rules for. (Ex. "Modern", "Standard", etc.)  
`mtgSet` - Which MTG set you want to pull cards from (Ex. "LEA", "8ED", etc.)(This GA only supports a single set presently.) 
`elitism` - When building next generation, keep the best genome and do not breed it 
`generations` - Number of generations for genome breeding  
`newChild` - If you should introduce a "new random child" to the generation  
`populationSize` - Number of genomes in a generation  
`swapChance` - Chance for 2 genomes to swap halves   
`mutationChance` - Chance for one of the cards to change to a random card from the card pool (Keep this to a low single digit) Defaults to `0.01`
`deckOutputFilePath` - Path to forge constructed decks ex. "..AppData\\Roaming\\Forge\\decks\\constructed\\"  
`forgeDir` - Location of the forge directory with jar file  
`forgeJar` - Name of jar file (include .jar)  
