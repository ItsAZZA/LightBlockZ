package com.itsazza.lightblockz.commands

import com.itsazza.lightblockz.LightBlockZ
import com.itsazza.lightblockz.util.BlockFinder
import com.itsazza.lightblockz.util.Cooldown
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.system.measureTimeMillis

class InspectCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return true
        val config = LightBlockZ.instance.config
        if (!player.hasPermission("lightblockz.inspect")) {
            player.sendMessage("§cNo permission!")
            return true
        }

        if (config.getBoolean("settings.inspect.cooldown.enabled") && !player.hasPermission("lightblockz.inspect.bypasscooldown")) {
            val cooldown = Cooldown.check(player.uniqueId, LightBlockZ.instance.config.getInt("settings.inspect.cooldown.time"))
            if (cooldown != null) {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                player.sendMessage("§cYou must wait $cooldown seconds before using this again...")
                return true
            }
        }

        val locations: List<Location>
        val time = measureTimeMillis {
            locations = BlockFinder.getLightBlocksAroundPlayer(player)
        }

        if (locations.isEmpty()) {
            player.sendMessage("§cNo light blocks found nearby!")
            return true
        }

        val showTime = config.getBoolean("settings.inspect.showTimeTaken")
        player.sendMessage("§eHighlighting ${locations.size} light blocks around you${if (showTime) " §7(${time}ms)" else ""}")
        highlightBlocks(locations)
        return true
    }

    companion object {
        private val iterations = LightBlockZ.instance.config.getInt("settings.inspect.iterations")
        fun highlightBlocks(locations: List<Location>) {
            locations.forEach {
                val world = it.world!!
                it.add(0.5, 0.5, 0.5)

                object : BukkitRunnable() {
                    private var i = 0
                    override fun run() {
                        i++
                        world.spawnParticle(Particle.LIGHT, it, 1)
                        if (i >= iterations) {
                            cancel()
                        }
                    }
                }.runTaskTimerAsynchronously(LightBlockZ.instance, 0L, 80L)
            }
        }
    }
}