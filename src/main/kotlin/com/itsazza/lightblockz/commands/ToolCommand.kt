package com.itsazza.lightblockz.commands

import de.tr7zw.changeme.nbtapi.NBTItem
import de.tr7zw.changeme.nbtapi.NBTContainer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object ToolCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("lightblockz.tool")) {
            sender.sendMessage("§cNo permission for the tool")
            return true
        }

        val breakingTool = ItemStack(Material.LIGHT).also {
            val meta = it.itemMeta!!
            meta.setDisplayName("§c§lLight Block Tool")
            meta.lore = arrayListOf(
                "§7Lets you edit the light",
                "§7blocks with ease",
                "§0 ",
                "§e§lL-CLICK §7to destroy",
                "§e§lR-CLICK §7to inspect",
                "§e§lSHIFT-L-CLICK §7to edit"
            )
            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            it.itemMeta = meta
        }

        val nbtItem = NBTItem(breakingTool)
        val level = NBTContainer("{BlockStateTag: {level: 0}}")
        nbtItem.mergeCompound(level)

        sender.inventory.addItem(nbtItem.item)
        return true
    }
}