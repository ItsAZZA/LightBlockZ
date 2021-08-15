package com.itsazza.lightblockz.util

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

fun Player.canBreak(block: Block) : Boolean {
    val event = BlockBreakEvent(block, this)
    Bukkit.getPluginManager().callEvent(event)
    return !event.isCancelled
}