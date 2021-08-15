package com.itsazza.lightblockz.events

import com.itsazza.lightblockz.menu.LightBlockLevelMenu
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object LightBlockEvent : Listener {
    @EventHandler
    fun onRightClickAir(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_AIR) return

        val item = event.item ?: return
        if (item.type != Material.LIGHT) return
        if (item.containsEnchantment(Enchantment.LUCK)) return
        if (!event.player.hasPermission("lightblockz.change.interact")) return
        LightBlockLevelMenu().open(event.player, LightBlockLevelMenu.EditMode.STACK, stack = item)
    }
}