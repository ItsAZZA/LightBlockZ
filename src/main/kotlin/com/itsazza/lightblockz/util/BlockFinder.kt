package com.itsazza.lightblockz.util

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

object BlockFinder {
    private fun getLightBlocksInChunk(chunk: Chunk) : List<Location> {
        val world = chunk.world
        val minHeight = world.minHeight
        val maxHeight = world.maxHeight
        val locations = mutableListOf<Location>()

        for (x in 0 until 16) {
            for (y in minHeight until maxHeight) {
                for (z in 0 until 16) {
                    val block = chunk.getBlock(x, y, z)
                    if (block.type == Material.LIGHT) {
                        locations += block.location
                    }
                }
            }
        }
        return locations
    }

    fun getLightBlocksAroundPlayer(player: Player) : List<Location> {
        val offset = arrayOf(-1, 0, 1)
        val world = player.world
        val playerChunk = player.location.chunk
        val baseX = playerChunk.x
        val baseZ = playerChunk.z

        val listOfLocations = mutableListOf<Location>()

        for (x in offset) {
            for (z in offset) {
                val chunk = world.getChunkAt(baseX + x, baseZ + z)
                listOfLocations += getLightBlocksInChunk(chunk)
            }
        }
        return listOfLocations
    }
}