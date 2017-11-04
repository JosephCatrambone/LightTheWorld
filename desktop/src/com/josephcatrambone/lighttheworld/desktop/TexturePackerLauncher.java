package com.josephcatrambone.lighttheworld.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerLauncher {
	public static void main (String[] arg) {
		TexturePacker.process("unpacked/", "android/assets/", "images.atlas");
	}
}
