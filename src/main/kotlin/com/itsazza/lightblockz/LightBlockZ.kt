package com.itsazza.lightblockz

import com.itsazza.lightblockz.commands.InspectCommand
import com.itsazza.lightblockz.commands.ReloadCommand
import com.itsazza.lightblockz.commands.ToolCommand
import com.itsazza.lightblockz.events.CraftEvent
import com.itsazza.lightblockz.events.CreativeInteractEvent
import com.itsazza.lightblockz.events.LightBlockEvent
import com.itsazza.lightblockz.events.ToolEvents
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class LightBlockZ : JavaPlugin() {
    companion object {
        lateinit var instance: LightBlockZ
            private set
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        Metrics(this, 12443)

        getCommand("lighttool")?.setExecutor(ToolCommand)
        getCommand("lightinspect")?.setExecutor(InspectCommand())
        getCommand("lightreload")?.setExecutor(ReloadCommand)
        Bukkit.getPluginManager().registerEvents(ToolEvents, this)
        Bukkit.getPluginManager().registerEvents(LightBlockEvent, this)
        Bukkit.getPluginManager().registerEvents(CreativeInteractEvent, this)
        if (config.getBoolean("settings.recipe.enabled")) {
            setupRecipe()
            Bukkit.getPluginManager().registerEvents(CraftEvent, this)
        }
    }

    fun getLangString(path: String) : String {
        return config.getString("messages.$path") ?: throw NullPointerException()
    }

    private fun setupRecipe() {
        val key = NamespacedKey(this, "light_block")
        val recipe = ShapedRecipe(key, ItemStack(Material.LIGHT, config.getInt("settings.recipe.amount")))

        val shape = config.getStringList("settings.recipe.shape")
        recipe.shape(*shape.toTypedArray())

        val ingredientKeys = config.getConfigurationSection("settings.recipe.ingredients")!!.getKeys(false)
        val ingredientCharacters = shape.joinToString("").toCharArray().distinct().filter{ it != ' '}.map { it.toString() }
        if (!ingredientKeys.containsAll(ingredientCharacters)) {
            logger.log(Level.SEVERE, "The ingredients in configuration don't match the recipe!")
            return
        }

        for (ingredientKey in ingredientKeys) {
            if (!ingredientCharacters.contains(ingredientKey)) continue
            val materialString = config.getString("settings.recipe.ingredients.$ingredientKey")!!
            val material = Material.matchMaterial(materialString)
            if (material == null) {
                logger.log(Level.SEVERE, "Could not find material for \"$materialString\"")
                return
            }
            recipe.setIngredient(ingredientKey.first(), material)
        }
        logger.log(Level.INFO, "Added crafting recipe for the light block!")
        Bukkit.addRecipe(recipe)
    }
}