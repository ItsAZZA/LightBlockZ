package com.itsazza.lightblockz

import com.itsazza.lightblockz.commands.HighlightCommand
import com.itsazza.lightblockz.commands.ToolCommand
import com.itsazza.lightblockz.events.ToolEvents
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class LightBlockZ : JavaPlugin() {
    companion object {
        lateinit var instance: LightBlockZ
            private set
    }

    override fun onEnable() {
        instance = this
        getCommand("lightblockz")?.setExecutor(ToolCommand)
        getCommand("test")?.setExecutor(HighlightCommand())
        Bukkit.getPluginManager().registerEvents(ToolEvents, this)

        val key = NamespacedKey(this, "light_block")
        val recipe = ShapedRecipe(key, ItemStack(Material.LIGHT, 4))
        recipe.shape("PGP", "GIG", "PGP")
        recipe.setIngredient('I', Material.IRON_NUGGET)
        recipe.setIngredient('P', Material.GLASS_PANE)
        recipe.setIngredient('G', Material.GLOWSTONE)
        Bukkit.addRecipe(recipe)
    }
}