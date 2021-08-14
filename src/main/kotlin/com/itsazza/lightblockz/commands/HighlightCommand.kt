package com.itsazza.lightblockz.commands

import com.itsazza.lightblockz.LightBlockZ
import com.itsazza.lightblockz.util.BlockFinder
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HighlightCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return true
        val locations = BlockFinder.getLightBlocksAroundPlayer(player)
        if (locations.isEmpty()) {
            player.sendMessage("§cNo light blocks found nearby!")
            return true
        }

        player.sendMessage("§eHighlighting ${locations.size} light blocks around you")
        highlightBlocks(locations)
        return true
    }

    companion object {
        fun highlightBlocks(locations: List<Location>) {
            locations.forEach {
                val world = it.world!!
                it.add(0.5, 0.5, 0.5)

                object : BukkitRunnable() {
                    private var i = 0
                    override fun run() {
                        i++
                        world.spawnParticle(Particle.LIGHT, it, 1)
                        if (i >= 4) {
                            cancel()
                        }
                    }
                }.runTaskTimerAsynchronously(LightBlockZ.instance, 0L, 80L)
            }
        }
    }
}