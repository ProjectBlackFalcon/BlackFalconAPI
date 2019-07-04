package com.ankamagames.dofus.core.movement;

public class MovementVelocity {
    public enum MovementTypeEnum
    {
        MOUNTED,
        PARABLE,
        RUNNING,
        SLIDE,
        WALKING
    }
    
    public static int getPathVelocity(MovementPath cells, MovementTypeEnum moveType)
    {
        int velocity = 0;
        for (PathElement cell : cells.Cells) {
			velocity += getVelocity(cell, moveType);
		}
        velocity *= 2;
        return velocity;
    }

    public static int getVelocity(PathElement cell, MovementTypeEnum moveType)
    {
        if (cell.Orientation % 2 == 0)
        {
            if (cell.Orientation % 4 == 0)
                switch (moveType)
                {
                    case MOUNTED:
                        return 200;
                    case PARABLE:
                        return 500;
                    case RUNNING:
                        return 255;
                    case SLIDE:
                        return 255 * 3;
                    case WALKING:
                        return 510;
                }

            // VERTICAL DIAGONAL VELOCITY

            switch (moveType)
            {
                case MOUNTED:
                    return 120;
                case PARABLE:
                    return 450;
                case RUNNING:
                    return 150;
                case SLIDE:
                    return 150 * 3;
                case WALKING:
                    return 425;
            }
        }

        // LINEAR VELOCITY

        switch (moveType)
        {
            case MOUNTED:
                return 135;
            case PARABLE:
                return 400;
            case RUNNING:
                return 170;
            case SLIDE:
                return 170 * 3;
            case WALKING:
                return 480;
        }

        return 0;
    }
    
}
