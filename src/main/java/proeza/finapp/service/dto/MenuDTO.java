package proeza.finapp.service.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuDTO {

	private String                  code;
	private String                  text;
	private final List<MenuItemDTO> items;

	public MenuDTO() {
		this.items = new ArrayList<>(0);
	}

	public MenuDTO(List<MenuItemDTO> items) {
		this.items = items;
	}

	public MenuDTO(List<MenuItemDTO> items, String code) {
		this.code = code;
		this.items = items;
	}

	public MenuDTO(List<MenuItemDTO> items, String code, String text) {
		this.code = code;
		this.text = text;
		this.items = items;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<MenuItemDTO> getItems() {
		return this.items;
	}

	public void addItem(MenuItemDTO item) {
		this.items.add(item);
	}
}