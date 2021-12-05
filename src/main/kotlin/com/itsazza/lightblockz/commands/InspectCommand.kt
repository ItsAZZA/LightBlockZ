package com.itsazza.lightblockz.commands

import com.itsazza.lightblockz.LightBlockZ.Companion.instance
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
        val config = instance.config
        if (!player.hasPermission("lightblockz.inspect.command")) {
            player.sendMessage(instance.getLangString("general-no-permission"))
            return true
        }

        if (config.getBoolean("settings.inspect.cooldown.enabled") && !player.hasPermission("lightblockz.inspect.bypasscooldown")) {
            val cooldown = Cooldown.check(player.uniqueId, config.getInt("settings.inspect.cooldown.time"))
            if (cooldown != null) {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                player.sendMessage(instance.getLangString("inspect-cooldown-wait").format(cooldown))
                return true
            }
        }

        val locations: List<Location>
        val offset = config.getInt("settings.inspect.offset")
        val verticalOffset = config.getInt("settings.inspect.verticalOffset")
        val time = measureTimeMillis {
            locations = BlockFinder.getLightBlocksAroundPlayer(player, offset, verticalOffset)
        }

        if (locations.isEmpty()) {
            player.sendMessage(instance.getLangString("inspect-noblocks"))
            return true
        }

        val showTime = config.getBoolean("settings.inspect.showTimeTaken")
        player.sendMessage(
            instance.getLangString("inspect-highlight").format(locations.size, if (showTime) " (${time}ms)" else "")
        )
        highlightBlocks(locations, player)
        return true
    }

    companion object {
        fun highlightBlocks(locations: List<Location>, player: Player) {
            val perPlayer = instance.config.getBoolean("settings.inspect.perPlayer")
            val iterations = instance.config.getInt("settings.inspect.iterations")
            val particle = Particle.values().firstOrNull { it.name == "LIGHT" }

            val playerParticles = fun(location: Location) {
                val world = location.world ?: return
                if (particle == null)
                    world.spawnParticle(Particle.BLOCK_MARKER, location, 1, world.getBlockAt(location).blockData)
                else
                    player.spawnParticle(particle, location, 1)
            }
            val worldParticles = fun(location: Location) {
                val world = location.world ?: return
                if (particle == null)
                    world.spawnParticle(Particle.BLOCK_MARKER, location, 1, world.getBlockAt(location).blockData)
                else
                    world.spawnParticle(particle, location, 1)
            }
            val finalCommand = if (perPlayer) playerParticles else worldParticles

            locations.forEach {
                it.add(0.5, 0.5, 0.5)
                object : BukkitRunnable() {
                    private var i = 0
                    override fun run() {
                        i++
                        finalCommand(it)
                        if (i >= iterations) {
                            cancel()
                        }
                    }
                }.runTaskTimerAsynchronously(instance, 0L, 80L)
            }
        }
    }
}