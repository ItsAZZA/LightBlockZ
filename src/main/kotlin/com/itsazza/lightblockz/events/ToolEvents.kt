package com.itsazza.lightblockz.events

import com.itsazza.lightblockz.commands.HighlightCommand
import com.itsazza.lightblockz.util.BlockFinder
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Waterlogged
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

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

        if (player.isSneaking) {
            // Open menu for editing
        } else {
            val data = block.blockData as Waterlogged
            if (data.isWaterlogged) {
                block.type = Material.WATER
            } else {
                block.type = Material.AIR
            }
            val location = block.location
            val world = location.world!!
            player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f)
            world.dropItem(location, ItemStack(Material.LIGHT))
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

        // TODO: Cooldown?
        val locations = BlockFinder.getLightBlocksAroundPlayer(player)
        if (locations.isEmpty()) {
            player.sendMessage("§cDidn't find any light blocks around you!")
            return
        }
        player.sendMessage("§eHighlighting ${locations.size} light blocks around you")
        HighlightCommand.highlightBlocks(locations)
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