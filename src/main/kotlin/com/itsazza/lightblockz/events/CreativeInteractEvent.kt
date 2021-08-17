package com.itsazza.lightblockz.events

import com.itsazza.lightblockz.menu.LightBlockLevelMenu
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object CreativeInteractEvent : Listener {
    @EventHandler
    fun onShiftRightClick(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.LEFT_CLICK_BLOCK) return
        if (!event.player.isSneaking) return
        if (event.player.gameMode != GameMode.CREATIVE) return
        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.type != Material.LIGHT) return
        if (!event.player.hasPermission("lightblockz.change.creative")) return
        LightBlockLevelMenu().open(event.player, LightBlockLevelMenu.EditMode.BLOCK, clickedBlock)
        event.isCancelled = true
    }
}