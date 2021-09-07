package com.itsazza.lightblockz.commands

import com.itsazza.lightblockz.LightBlockZ.Companion.instance
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("lightblockz.reload")) return true
        instance.reloadConfig()
        sender.sendMessage(instance.getLangString("general-reload"))
        return true
    }
}