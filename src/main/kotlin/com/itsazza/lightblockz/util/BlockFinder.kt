package com.itsazza.lightblockz.util

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

object BlockFinder {
    private fun getLightBlocksInChunk(chunk: Chunk, playerLocation: Location, verticalOffset: Int): List<Location> {
        val world = chunk.world
        val playerY = playerLocation.blockY
        val minHeight = (playerY - verticalOffset).coerceAtLeast(world.minHeight)
        val maxHeight = (playerY + verticalOffset).coerceAtMost(world.maxHeight)
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

    fun getLightBlocksAroundPlayer(player: Player, offset: Int = 0, verticalOffset: Int): List<Location> {
        if (offset == 0) return getLightBlocksInChunk(player.location.chunk, player.location, verticalOffset)
        val offsetArray = IntRange(-offset, offset).step(1).toList()

        val world = player.world
        val playerChunk = player.location.chunk
        val baseX = playerChunk.x
        val baseZ = playerChunk.z

        val listOfLocations = mutableListOf<Location>()

        for (x in offsetArray) {
            for (z in offsetArray) {
                val chunk = world.getChunkAt(baseX + x, baseZ + z)
                listOfLocations += getLightBlocksInChunk(chunk, player.location, verticalOffset)
            }
        }
        return listOfLocations
    }
}