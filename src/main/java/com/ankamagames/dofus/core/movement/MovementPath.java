package com.ankamagames.dofus.core.movement;

import java.util.ArrayList;
import java.util.List;

public class MovementPath {

	public MapPoint CellEnd;
	public MapPoint CellStart;
	public List<PathElement> Cells = new ArrayList<PathElement>();

	// Methods
	public void compress() {
		if (Cells.size() > 0) {
			int i = Cells.size() - 1;
			while (i > 0) {
				if (Cells.get(i).Orientation == Cells.get(i - 1).Orientation) {
					Cells.remove(i);
					i -= 1;
				}
				i -= 1;
			}
		}
	}

	@Override
	public String toString() {
		String s = "Movement Path : ";
		for (PathElement pathElement : Cells) {
			s += pathElement.Cell.CellId + " ";
		}
		return s;
	}

}
