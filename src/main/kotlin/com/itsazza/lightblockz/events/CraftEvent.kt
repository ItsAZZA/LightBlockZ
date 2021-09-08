package com.itsazza.lightblockz.events

import com.itsazza.lightblockz.LightBlockZ.Companion.instance
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.CraftingInventory

object CraftEvent : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onCraft(event: InventoryClickEvent) {
        val craftingInventory = event.inventory as? CraftingInventory ?: return
        if (event.slot != 0) return

        val result = craftingInventory.result ?: return
        if (result.type == Material.LIGHT) {
            val player = event.whoClicked as? Player ?: return
            if (!player.hasPermission("lightblockz.craft")) {
                event.isCancelled = true
                player.sendMessage(instance.getLangString("general-no-craft-permission"))
            }
        }
    }
}