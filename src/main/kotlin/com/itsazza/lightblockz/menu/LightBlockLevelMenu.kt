package com.itsazza.lightblockz.menu

import com.itsazza.lightblockz.LightBlockZ
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import de.tr7zw.changeme.nbtapi.NBTContainer
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Light
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object LightBlockLevelMenu {
    lateinit var block: Block

    fun open(player: Player, block: Block) {
        this.block = block
        create().show(player)
    }

    private fun create() : InventoryGui {
        val gui = InventoryGui(
            LightBlockZ.instance,
            "",
            arrayOf(
                " 0000000 ",
                " 0000000 ",
                " 0000000 ",
                "    c    ",
            )
        )

        gui.addElement(
            'c',
            ItemStack(Material.BARRIER),
            {
                it.gui.destroy()
                return@addElement true
            },
            "§c§lClose"
        )

        val group = GuiElementGroup('0')
        for (i in 0..15) {
            group.addElement(createLightButton(i))
        }

        gui.addElement(group)
        gui.setFiller(ItemStack(Material.YELLOW_STAINED_GLASS_PANE))
        gui.setCloseAction { false }
        return gui
    }

    private fun createLightButton(level: Int) : StaticGuiElement {
        val item = NBTItem(ItemStack(Material.LIGHT)).also {
            val tag = NBTContainer("{BlockStateTag: {level: $level}}")
            it.mergeCompound(tag)
        }.item

        return StaticGuiElement(
            '!',
            item,
            {
                // This somehow sets the light level of the block?!
                val data = block.blockData as Light
                data.level = level
                // block.blockData = data
                return@StaticGuiElement true
            },
            "§6§lLight Level $level",
            "§7Sets the block's light level",
            "§7to a power of $level/15",
            "§0 ",
            "§eClick to apply!"
        )
    }
}