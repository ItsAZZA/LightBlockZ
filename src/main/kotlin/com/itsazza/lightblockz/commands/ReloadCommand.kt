package com.itsazza.lightblockz.commands

import com.itsazza.lightblockz.LightBlockZ
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("lightblockz.reload")) return true
        LightBlockZ.instance.reloadConfig()
        sender.sendMessage("§eReloaded config!")
        return true
    }
}