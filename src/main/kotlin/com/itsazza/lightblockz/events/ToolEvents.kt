package com.itsazza.lightblockz.events

import com.itsazza.lightblockz.LightBlockZ
import com.itsazza.lightblockz.commands.InspectCommand
import com.itsazza.lightblockz.menu.LightBlockLevelMenu
import com.itsazza.lightblockz.util.BlockFinder
import com.itsazza.lightblockz.util.Cooldown
import com.itsazza.lightblockz.util.canBreak
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Waterlogged
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.system.measureTimeMillis

object ToolEvents : Listener {
    @EventHandler
    fun onLeftClickTool(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.LEFT_CLICK_BLOCK) return

        val player = event.player
        if (player.gameMode != GameMode.SURVIVAL) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.LIGHT) return

        val item = event.item ?: return
        if (!item.enchantments.containsKey(Enchantment.LUCK)) return

        if (!event.player.canBreak(block)) {
            event.player.sendMessage("§cNo permission to build here!")
            return
        }

        if (player.isSneaking) {
            if (!player.hasPermission("lightblockz.change.interact")) return
            LightBlockLevelMenu().open(player, LightBlockLevelMenu.EditMode.BLOCK, block)
        } else {
            val data = event.clickedBlock!!.blockData as Waterlogged
            if (data.isWaterlogged) block.type = Material.WATER else block.type = Material.AIR

            val location = block.location
            val world = location.world!!
            world.dropItem(location, ItemStack(Material.LIGHT))
            player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onRightClickTool(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val player = event.player
        if (player.gameMode != GameMode.SURVIVAL) return

        val item = event.item ?: return
        if (!item.enchantments.containsKey(Enchantment.LUCK)) return

        val config = LightBlockZ.instance.config
        if (config.getBoolean("settings.inspect.cooldown.enabled") && !player.hasPermission("lightblockz.inspect.bypasscooldown")) {
            val cooldown = Cooldown.check(player.uniqueId, config.getInt("settings.inspect.cooldown.time"))
            if (cooldown != null) {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                player.sendMessage("§cYou must wait $cooldown seconds before using this again...")
                return
            }
        }

        val locations: List<Location>
        val time = measureTimeMillis {
            locations = BlockFinder.getLightBlocksAroundPlayer(player)
        }

        if (locations.isEmpty()) {
            player.sendMessage("§cNo light blocks found nearby!")
            return
        }

        val showTime = config.getBoolean("settings.inspect.showTimeTaken")
        player.sendMessage("§eHighlighting ${locations.size} light blocks around you${if (showTime) " §7(${time}ms)" else ""}")
        InspectCommand.highlightBlocks(locations)
    }

    @EventHandler
    fun onToolPlaceAttempt(event: BlockPlaceEvent) {
        if (event.blockPlaced.type != Material.LIGHT) return
        val mainHandMeta = event.player.inventory.itemInMainHand.itemMeta
        val offHandMeta = event.player.inventory.itemInOffHand.itemMeta
        if (mainHandMeta != null && mainHandMeta.hasEnchant(Enchantment.LUCK) ||
            offHandMeta != null && offHandMeta.hasEnchant(Enchantment.LUCK)
        ) {
            event.isCancelled = true
        }
    }
}