package com.ankamagames.dofus.core.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pathfinder {
	
    private Map map;

	private int DCost = 15;
	private int HeuristicCost = 10;
	private int HVCost = 10;

	// Fields
	private boolean IsFighting = false;
	private List<CellInfo> MapStatus = new ArrayList<CellInfo>();
	private int MaxX = 34;
	private int MaxY = 14;

	private int MinX = 0;
	private int MinY = -19;
	private boolean AllowDiag;
	private boolean AllowDiagCornering;
	private boolean AllowTroughEntity = true;
	private MapPoint AuxEndPoint;
	private int AuxEndX;
	private int AuxEndY;

	private int DistanceToEnd;
	private MapPoint EndPoint;
	private int EndX;
	private int EndY;
	private MovementPath MovPath;
	private int NowX;
	private int NowY;
	private List<OpenSquare> OpenList = new ArrayList<OpenSquare>();
	private int PreviousCellId;

	private MapPoint StartPoint;

	private int StartX;
	private int StartY;

	public Pathfinder(final Map map)
	{
		this.map = map;
	}

	public MovementPath findPath(int FromCell, int ToCell, boolean ate) {
		return findPath(FromCell, ToCell, ate, true);
	}

	public MovementPath findPath(int FromCell, int ToCell) {
		return findPath(FromCell, ToCell, true, true);
	}

	// Bool : ate => Throug entity
	public MovementPath findPath(int FromCell, int ToCell, boolean ate, boolean adc) {
		MovPath = new MovementPath();
		MovPath.CellStart = new MapPoint(FromCell);
		MovPath.CellEnd = new MapPoint(ToCell);
		AllowDiag = adc;
		AllowDiagCornering = adc;
		AllowTroughEntity = ate;

		startPathfinding(new MapPoint(FromCell), new MapPoint(ToCell));
		processPathfinding();

		return MovPath;
	}

	public void startPathfinding(MapPoint startP, MapPoint endP) {
		StartX = startP.X;
		StartY = startP.Y;
		EndX = endP.X;
		EndY = endP.Y;

		StartPoint = new MapPoint(startP.X, startP.Y);
		EndPoint = new MapPoint(endP.X, endP.Y);

		AuxEndPoint = StartPoint;
		AuxEndX = StartPoint.X;
		AuxEndY = StartPoint.Y;

		DistanceToEnd = StartPoint.DistanceToCell(EndPoint);

		for (int y = -19; y <= MaxY; y++)
			for (int x = 0; x <= MaxX; x++)
				MapStatus.add(new CellInfo(0, null, false, false, x, y));
		OpenList = new ArrayList<OpenSquare>();
		openSquare(StartY, StartX, null, 0, 0, false);
	}

	public void processPathfinding()
    {
        int actualX = 0;
        int actualY = 0;
        int speed = 0;
        int moveCost = 0;

        boolean isDownRightEnd = false;
        boolean isDownRightStart = false;
        boolean isTopRightEnd = false;
        boolean isTopRightStart = false;

        MapPoint actualPoint = null;
        int actualDistanceToEnd = 0;
        double heuristic = 0;
        int square = 0;

        if (OpenList.size() > 0 && !isClosed(EndY, EndX))
        {
            square = nearerSquare();
            NowY = OpenList.get(square).Y;
            NowX = OpenList.get(square).X;
            PreviousCellId = new MapPoint(NowX, NowY).CellId;
            closeSquare(NowY, NowX);

            for (actualY = NowY - 1; actualY <= NowY + 1; actualY++)
            for (actualX = NowX - 1; actualX <= NowX + 1; actualX++)
                if (new MapPoint(actualX, actualY).IsInMap())
                    if (actualY >= MinY && actualY < MaxY && actualX >= MinX && actualX < MaxX &&
                        !(actualY == NowY && actualX == NowX) &&
                        (AllowDiag || actualY == NowY || actualX == NowX &&
                         (AllowDiagCornering || actualY == NowY || actualX == NowX ||
                          pointMov(NowX, NowY, PreviousCellId, AllowTroughEntity) ||
                          pointMov(actualX, NowY, PreviousCellId, AllowTroughEntity))))
                        if (!(!pointMov(NowX, actualY, PreviousCellId, AllowTroughEntity) &&
                              !pointMov(actualX, NowY, PreviousCellId, AllowTroughEntity) && !IsFighting &&
                              AllowDiag))
                            if (pointMov(actualX, actualY, PreviousCellId, AllowTroughEntity))
                                if (!isClosed(actualY, actualX))
                                {
                                    if (actualX == EndX && actualY == EndY)
                                        speed = 1;
                                    else
                                        speed = (int) getCellSpeed(new MapPoint(actualX, actualY).CellId,
                                            AllowTroughEntity);

                                    moveCost = getCellInfo(NowY, NowX).MovementCost +
                                               (actualY == NowY || actualX == NowX ? HVCost : DCost) * speed;

                                    if (AllowTroughEntity)
                                    {
                                        isDownRightEnd = actualX + actualY == EndX + EndY;
                                        isDownRightStart = actualX + actualY == StartX + StartY;
                                        isTopRightEnd = actualX - actualY == EndX - EndY;
                                        isTopRightStart = actualX - actualY == StartX - StartY;
                                        actualPoint = new MapPoint(actualX, actualY);

                                        if (!isDownRightEnd && !isTopRightEnd ||
                                            !isDownRightStart && !isTopRightStart)
                                        {
                                            moveCost = moveCost + actualPoint.DistanceToCell(EndPoint);
                                            moveCost = moveCost + actualPoint.DistanceToCell(StartPoint);
                                        }

                                        if (actualX == EndX || actualY == EndY)
                                            moveCost = moveCost - 3;
                                        if (isDownRightEnd || isTopRightEnd || actualX + actualY == NowX + NowY ||
                                            actualX - actualY == NowX - NowY)
                                            moveCost = moveCost - 2;
                                        if (actualX == StartX || actualY == StartY)
                                            moveCost = moveCost - 3;
                                        if (isDownRightStart || isTopRightStart)
                                            moveCost = moveCost - 2;

                                        actualDistanceToEnd = actualPoint.DistanceToCell(EndPoint);
                                        if (actualDistanceToEnd < DistanceToEnd)
                                            if (actualX == EndX || actualY == EndY ||
                                                actualX + actualY == EndX + EndY ||
                                                actualX - actualY == EndX - EndY)
                                            {
                                                AuxEndPoint = actualPoint;
                                                AuxEndX = actualX;
                                                AuxEndY = actualY;
                                                DistanceToEnd = actualDistanceToEnd;
                                            }
                                    }

                                    if (isOpened(actualY, actualX))
                                    {
                                        if (moveCost < getCellInfo(actualY, actualX).MovementCost)
                                            openSquare(actualY, actualX, new int [] {NowY, NowX}, moveCost, 0, true);
                                    }
                                    else
                                    {
                                        heuristic = (double) (HeuristicCost) *
                                                    Math.sqrt((EndY - actualY) * (EndY - actualY) +
                                                              (EndX - actualX) * (EndX - actualX));
                                        openSquare(actualY, actualX, new int [] {NowY, NowX}, moveCost, heuristic,false);
                                    }
                                }
            processPathfinding();
        }
        else
        {
            endPathfinding();
        }
    }
	
    public void endPathfinding()
    {
    	List<MapPoint> mapsArray = new ArrayList<MapPoint>();
        int parentY = 0;
        int parentX = 0;
        MapPoint btwPoint = null;
        List<MapPoint> tempArray = new ArrayList<MapPoint>();
        int i = 0;
        int actualX = 0;
        int actualY = 0;
        int thirdX = 0;
        int thirdY = 0;
        int btwX = 0;
        int btwY = 0;
        boolean endPointClosed = isClosed(EndY, EndX);

        if (!endPointClosed)
        {
            EndPoint = AuxEndPoint;
            EndX = AuxEndX;
            EndY = AuxEndY;
            endPointClosed = true;
            MovPath.CellEnd = EndPoint;
        }
        PreviousCellId = -1;
        if (endPointClosed)
        {
            NowX = EndX;
            NowY = EndY;

            while (!(NowX == StartX) || !(NowY == StartY))
            {
                mapsArray.add(new MapPoint(NowX, NowY));
                parentY = getCellInfo(NowY, NowX).Parent[0];
                parentX = getCellInfo(NowY, NowX).Parent[1];
                NowX = parentX;
                NowY = parentY;
            }
            mapsArray.add(StartPoint);
            if (AllowDiag)
            {
                for (i = 0; i < mapsArray.size(); i++)
                {
                    tempArray.add(mapsArray.get(i));
                    PreviousCellId = mapsArray.get(i).CellId;
                    if (mapsArray.size() > i + 2 && !(mapsArray.get(i+2) == null) &&
                        mapsArray.get(i).DistanceToCell(mapsArray.get(i + 2)) == 1 &&
                        !isChangeZone(mapsArray.get(i).CellId, mapsArray.get(i+1).CellId) &&
                        !isChangeZone(mapsArray.get(i+1).CellId, mapsArray.get(i+2).CellId))
                    {
                        i += 1;
                    }
                    else
                    {
                        if (mapsArray.size() > i + 3 && !(mapsArray.get(i+3) == null) &&
                            mapsArray.get(i).DistanceToCell(mapsArray.get(i+3)) == 2)
                        {
                            actualX = mapsArray.get(i).X;
                            actualY = mapsArray.get(i).Y;
                            thirdX = mapsArray.get(i+3).X;
                            thirdY = mapsArray.get(i+3).Y;
                            btwX = actualX + (int) Math.round((thirdX - actualX) / 2.0);
                            btwY = actualY + (int) Math.round((thirdY - actualY) / 2.0);
                            btwPoint = new MapPoint(btwX, btwY);
                            if (pointMov(btwX, btwY, PreviousCellId, true) &&
                                getCellSpeed(btwPoint.CellId, AllowTroughEntity) < 2)
                            {
                                tempArray.add(btwPoint);
                                PreviousCellId = btwPoint.CellId;
                                i += 2;
                            }
                        }
                        else
                        {
                            if (mapsArray.size() > i + 2 && !(mapsArray.get(i + 2) == null) &&
                                mapsArray.get(i).DistanceToCell(mapsArray.get(i)) == 2)
                            {
                                actualX = mapsArray.get(i).X;
                                actualY = mapsArray.get(i).Y;
                                thirdX = mapsArray.get(i + 2).X;
                                thirdY = mapsArray.get(i + 2).Y;
                                btwX = mapsArray.get(i + 1).X;
                                btwY = mapsArray.get(i + 2).Y;

                                if (actualX + actualY == thirdX + thirdY && !(actualX - actualY == btwX - btwY) &&
                                    !isChangeZone(new MapPoint(actualX, actualY).CellId,
                                        new MapPoint(btwX, btwY).CellId) &&
                                    !isChangeZone(new MapPoint(btwX, btwY).CellId,
                                        new MapPoint(thirdX, thirdY).CellId))
                                {
                                    i += 1;
                                }
                                else
                                {
                                    if (actualX - actualY == thirdX - thirdY &&
                                        !(actualX - actualY == btwX - btwY) &&
                                        !isChangeZone(new MapPoint(actualX, actualY).CellId,
                                            new MapPoint(btwX, btwY).CellId) &&
                                        !isChangeZone(new MapPoint(btwX, btwY).CellId,
                                            new MapPoint(thirdX, thirdY).CellId))
                                    {
                                        i += 1;
                                    }
                                    else
                                    {
                                        if (actualX == thirdX && !(actualX == btwX) &&
                                            getCellSpeed(new MapPoint(actualX, btwY).CellId, AllowTroughEntity) <
                                            2 && pointMov(actualX, btwY, PreviousCellId, AllowTroughEntity))
                                        {
                                            btwPoint = new MapPoint(actualX, btwY);
                                            tempArray.add(btwPoint);
                                            PreviousCellId = btwPoint.CellId;
                                            i += 1;
                                        }
                                        else
                                        {
                                            if (actualY == thirdY && !(actualY == btwY) &&
                                                getCellSpeed(new MapPoint(btwX, actualY).CellId,
                                                    AllowTroughEntity) < 2 &&
                                                pointMov(btwX, actualY, PreviousCellId, AllowTroughEntity))
                                            {
                                                btwPoint = new MapPoint(btwX, actualY);
                                                tempArray.add(btwPoint);
                                                PreviousCellId = btwPoint.CellId;
                                                i += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                mapsArray = tempArray;
            }
            if (mapsArray.size() == 1)
                mapsArray = new ArrayList<MapPoint>();
            Collections.reverse(mapsArray);
            movementPathFromArray(mapsArray.toArray(new MapPoint [mapsArray.size()]));
        }
    }

	public boolean pointMov(int x, int y, int cellId, boolean troughtEntities) {
		boolean isNewSystem = this.map.isUsingNewMovementSystem();
		MapPoint actualPoint = new MapPoint(x, y);
		CellData fCellData = null;
		CellData sCellData = null;
		boolean mov = false;
		int floor = 0;

		if (actualPoint.IsInMap()) {
			fCellData = this.map.getCells().get(actualPoint.CellId);
			mov = fCellData.isMov() && (!IsFighting || !fCellData.isNonWalkableDuringFight());

			if (!(mov == false) && isNewSystem && cellId != -1 && cellId != actualPoint.CellId) {
				sCellData = this.map.getCells().get(cellId);
				floor = (int) Math.abs(Math.abs(fCellData.getFloor()) - Math.abs(sCellData.getFloor()));
				if (!(sCellData.getMoveZone() == fCellData.getMoveZone()) && floor > 0 || sCellData.getMoveZone() == fCellData.getMoveZone()
						&& fCellData.getMoveZone() == 0 && floor > 11)
					mov = false;
			}
		} else {
			mov = false;
		}
		return mov;
	}
	
    public boolean isOpened(int y, int x)
    {
        return getCellInfo(y, x).Opened;
    }
	
    private double getCellSpeed(int cellId, boolean throughEntities)
    {
        int speed = (int) this.map.getCells().get(cellId).getSpeed();
        MapPoint cell = new MapPoint(cellId);

        if (throughEntities)
        {
            if (!this.map.NoEntitiesOnCell(cellId))
                return 20;

            if (speed >= 0)
                return 1 + (5 - speed);

            return 1 + 11 + Math.abs(speed);
        }

        double cost = 1.0D;
        MapPoint adjCell = null;

        if (!this.map.NoEntitiesOnCell(cellId))
            cost += 0.3;

        adjCell = new MapPoint(cell.X + 1, cell.Y);
        if (adjCell != null && !this.map.NoEntitiesOnCell(adjCell.CellId))
            cost += 0.3;

        adjCell = new MapPoint(cell.X, cell.Y + 1);
        if (adjCell != null && !this.map.NoEntitiesOnCell(adjCell.CellId))
            cost += 0.3;

        adjCell = new MapPoint(cell.X - 1, cell.Y);
        if (adjCell != null && !this.map.NoEntitiesOnCell(adjCell.CellId))
            cost += 0.3;

        adjCell = new MapPoint(cell.X, cell.Y - 1);
        if (adjCell != null && !this.map.NoEntitiesOnCell(adjCell.CellId))
            cost += 0.3;

        return cost;
    }

	public int nearerSquare() {
		int y = 0;
		double distance = 9999999;
		double tempDistance = 0;

		for (int tempY = 0; tempY < OpenList.size(); tempY++) {
			tempDistance = getCellInfo(OpenList.get(tempY).Y, OpenList.get(tempY).X).Heuristic
					+ getCellInfo(OpenList.get(tempY).Y, OpenList.get(tempY).X).MovementCost;
			if (tempDistance <= distance) {
				distance = tempDistance;
				y = tempY;
			}
		}
		return y;
	}
	
    public boolean isChangeZone(int fCell, int sCell)
    {
        return this.map.getCells().get(fCell).getMoveZone() != this.map.getCells().get(sCell).getMoveZone() &&
               Math.abs(this.map.getCells().get(fCell).getFloor()) == Math.abs(this.map.getCells().get(sCell).getFloor());
    }

	public void closeSquare(int y, int x) {
		for(int i = 0; i < OpenList.size() ; i++){
			if (OpenList.get(i).X == x && OpenList.get(i).Y == y){
				OpenList.remove(i);
			}
		}
		CellInfo cell = getCellInfo(y, x);
		cell.Opened = false;
		cell.Closed = true;
	}

	public void openSquare(int y, int x, int[] parent, int moveCost, double heuristic, boolean newSquare) {
		if (!newSquare)
			for (OpenSquare op : OpenList) {
				if (op.Y == y && op.X == x) {
					newSquare = true;
					break;
				}
			}

		if (!newSquare) {
			OpenList.add(new OpenSquare(y, x));
			for(int i = 0; i < MapStatus.size() ; i++){
				if (MapStatus.get(i).X == x && MapStatus.get(i).Y == y) {
					MapStatus.remove(i);
				}
			}
			MapStatus.add(new CellInfo(heuristic, null, true, false, x, y));
		}

		CellInfo cell = getCellInfo(y, x);
		cell.Parent = parent;
		cell.MovementCost = moveCost;
	}

	public CellInfo getCellInfo(int y, int x) {
		CellInfo cell = null;
		try {
			for (CellInfo c : MapStatus) {
				if (c.X == x && c.Y == y) {
					cell = c;
					break;
				}
			}
		} catch (Exception e) {
			cell = null;
		}
		return cell;
	}

	public boolean isClosed(int y, int x) {
		CellInfo cellInfo = getCellInfo(y, x);
		if (cellInfo == null || !cellInfo.Closed)
			return false;
		return true;
	}
	
    public void movementPathFromArray(MapPoint[] squares)
    {
        PathElement path = null;

        for (int i = 0; i <= squares.length - 2; i++)
        {
            path = new PathElement();
            path.Cell = squares[i];
            path.Orientation = squares[i].OrientationTo(squares[i + 1]);
            MovPath.Cells.add(path);
        }
//        System.out.println("---------------NOT COMPRESSED------------------");
//        System.out.println(MovPath.Cells.size());
//        for (PathElement cells : MovPath.Cells) {
//            System.out.println(cells.Cell.CellId + " - Orientation : " + cells.Orientation);
//		}
//        System.out.println("\n\n---------------COMPRESSED------------------");
        MovPath.compress();
//        for (PathElement cells : MovPath.Cells) {
//			this.network.append(cells.Cell.CellId + " - Orientation : " + cells.Orientation);
//		}
    }
}
