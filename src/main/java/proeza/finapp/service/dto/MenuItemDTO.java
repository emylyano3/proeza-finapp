package proeza.finapp.service.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemDTO implements Comparable<MenuItemDTO> {

	public MenuItemDTO () {
	}

	private String				text;
	private String				code;
	private String				href;
	private String				icon;
	private int					index;
	private List<MenuItemDTO>	subitems;
	private boolean				enabled;

	public String getText () {
		return this.text;
	}

	public void setText (String text) {
		this.text = text;
	}

	public String getCode () {
		return this.code;
	}

	public void setCode (String code) {
		this.code = code;
	}

	public String getHref () {
		return this.href;
	}

	public void setHref (String href) {
		this.href = href;
	}

	public String getIcon () {
		return this.icon;
	}

	public void setIcon (String icon) {
		this.icon = icon;
	}

	public int getIndex () {
		return this.index;
	}

	public void setIndex (int index) {
		this.index = index;
	}

	public List<MenuItemDTO> getSubitems () {
		return this.subitems;
	}

	public void setSubitems (List<MenuItemDTO> subitems) {
		this.subitems = subitems;
	}

	public boolean isEnabled () {
		return this.enabled;
	}

	public void setEnabled (boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int compareTo (@Nullable MenuItemDTO o) {
		if (o == null) {
			return -1;
		}
		return this.getIndex() - o.getIndex();
	}
}