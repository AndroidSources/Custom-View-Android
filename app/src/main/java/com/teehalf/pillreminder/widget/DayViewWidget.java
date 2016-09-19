package com.teehalf.pillreminder.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.teehalf.pillreminder.util.RxBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gowtham Chandrasekar on 19-09-2016.
 */
public class DayViewWidget extends View {

    public interface
    DayViewEntry {
        public String getName();

        public String getLabel();

        public int getIcon();

        public void menuActiviated();
    }

    Context context;


    //list of all my entries in the RadialWidget which means no. of wedges inside the radial widget
    private List<DayViewEntry> menuEntries = new ArrayList<DayViewEntry>();

    //There is a center circle in the radial menu widget
    private DayViewEntry centerCircle = null;

    //this is how we get the screen density
    private float screen_density = getContext().getResources().getDisplayMetrics().density;

    //the color of the entire circle itself -- by default it is blue color
    //default color of wedge pieces
    private int defaultColor = Color.rgb(255, 255, 255);
    //transparency of the colors, 255=Opague, 0=Transparent. this is the transparency of the circle color
    private int defaultAlpha = 255;

    //this color was getting used when the single wedge is getting selected.
    // The selected wedge and all of his child view are applied with this color
    //default color of wedge pieces
    private int wedge2Color = Color.rgb(50, 50, 50);
    private int wedge2Alpha = 255;


    //this outline color is used for the lines between the wedges. and the cirlce border color.
    //color of outline
    private int outlineColor = Color.rgb(150, 150, 150);
    //transparency of outline
    private int outlineAlpha = 255;

    //when selected at the center of the circle. This circle is used.
    private int selectedColor = Color.rgb(70, 130, 180);
    //transparency of fill when something is selected
    private int selectedAlpha = 255;

    //this is the color that is applied to all other wedges except the selected item.
    //color to fill when something is selected
    private int disabledColor = Color.rgb(34, 96, 120);
    //transparency of fill when something is selected
    private int disabledAlpha = 255;

    //transparency of images
    private int pictureAlpha = 255;

    //Text color across the view
    private int textColor = Color.rgb(34, 96, 120);
    ;
    //alpha value of the text color
    private int textAlpha = 255;

    //as of now header is not being used.
    private int headerTextColor = Color.rgb(255, 255, 255);    //color of header text
    private int headerTextAlpha = 255;                            //transparency of header text
    private int headerBackgroundColor = Color.rgb(0, 0, 0);    //color of header background
    private int headerBackgroundAlpha = 255;                    //transparency of header background


    //Number of wedges. Initially we are keeping the wedge count as 1
    private int wedgeQty = 1;

    //Based on the #wedgeQty figuring the number of wedges in total.
    private Wedge[] Wedges = new Wedge[wedgeQty];
    //Keeps track of which wedge is selected
    private Wedge selected = null;
    //Keeps track of which wedge is enabled for outer ring
    private Wedge enabled = null;
    //Rect class represents the rectangle
    private Rect[] iconRect = new Rect[wedgeQty];

    //Number of wedges
    private int wedgeQty2 = 1;
    private Wedge[] Wedges2 = new Wedge[wedgeQty2];
    //Keeps track of which wedge is selected
    private Wedge selected2 = null;
    private Rect[] iconRect2 = new Rect[wedgeQty2];
    //Keeps track off which menuItem data is being used for the outer ring
    private DayViewEntry wedge2Data = null;


    //Radius of inner ring size
    private int MinSize = scalePX(45);
    //Radius of outer ring size
    private int MaxSize = scalePX(150);
    //Radius of inner second ring size..this is nothing but the space between parent ring and the child ring
    private int r2MinSize = MaxSize + scalePX(10);
    //Radius of outer second ring size
    private int r2MaxSize = r2MinSize + scalePX(55);

    //Min Size of Image in Wedge
    private int MinIconSize = scalePX(20);
    //Max Size of Image in Wedge
    private int MaxIconSize = scalePX(20);
    //Inner Circle Radius
    private int cRadius = MinSize - scalePX(17);
    //TextSize
    private int textSize = scalePX(15);
    private int animateTextSize = textSize;

    //Center X location of Radial Menu
    private int xPosition = scalePX(120);
    //Center Y location of Radial Menu
    private int yPosition = scalePX(120);

    //Source X of clicked location
    private int xSource = 0;
    //Center Y of clicked location
    private int ySource = 0;
    //Display icon where at source location
    private boolean showSource = false;


    //Identifies touch event was in first wedge
    private boolean inWedge = false;
    //Identifies touch event was in second wedge
    private boolean inWedge2 = false;
    //Identifies touch event was in middle circle
    private boolean inCircle = false;


    //Identifies 2nd wedge is drawn
    private boolean Wedge2Shown = false;

    //Identifies if header box is drawn
    private boolean HeaderBoxBounded = false;

    private String headerString = null;
    private int headerTextSize = textSize;                //TextSize
    private int headerBuffer = scalePX(8);
    private Rect textRect = new Rect();
    private RectF textBoxRect = new RectF();
    private int headerTextLeft;
    private int headerTextBottom;

    private RotateAnimation rotate;
    private AlphaAnimation blend;
    private ScaleAnimation scale;
    private TranslateAnimation move;
    private AnimationSet spriteAnimation;
    private long animationSpeed = 400L;


    private static final int ANIMATE_IN = 1;
    private static final int ANIMATE_OUT = 2;

    private int animateSections = 4;
    private int r2VariableSize;
    private boolean animateOuterIn = false;
    private boolean animateOuterOut = false;

    public DayViewWidget(Context context) {
        super(context);
        this.context = context;
        // Gets screen specs and defaults to center of screen
        this.xPosition = (getResources().getDisplayMetrics().widthPixels) / 2;
        this.yPosition = (getResources().getDisplayMetrics().heightPixels) / 2;

        determineWedges();
    }


    private void determineWedges() {


        //number of redial menu entries
        int entriesQty = menuEntries.size();
        if (entriesQty > 0) {
            wedgeQty = entriesQty;

            float degSlice = 360 / wedgeQty;

            float start_degSlice = 270 - (degSlice / 2);

            //calculates where to put the images
            double rSlice = (2 * Math.PI) / wedgeQty;
            double rStart = (2 * Math.PI) * (0.75) - (rSlice / 2);

            this.Wedges = new Wedge[wedgeQty];
            this.iconRect = new Rect[wedgeQty];

            for (int i = 0; i < Wedges.length; i++) {

                this.Wedges[i] = new Wedge(xPosition, yPosition, MinSize, entriesQty == 7 ? MaxSize - 5 : MaxSize, (i
                        * degSlice) + start_degSlice, entriesQty == 7 ? degSlice + 3 : degSlice);
                float xCenter = (float) (Math.cos(((rSlice * i) + (rSlice * 0.5)) + rStart) * (MaxSize + MinSize) / 2) + xPosition;
                float yCenter = (float) (Math.sin(((rSlice * i) + (rSlice * 0.5)) + rStart) * (MaxSize + MinSize) / 2) + yPosition;

                int h = MaxIconSize;
                int w = MaxIconSize;
                if (menuEntries.get(i).getIcon() != 0) {
                    Drawable drawable = getResources().getDrawable(menuEntries.get(i).getIcon());
                    h = getIconSize(drawable.getIntrinsicHeight(), MinIconSize, MaxIconSize);
                    w = getIconSize(drawable.getIntrinsicWidth(), MinIconSize, MaxIconSize);
                }

                this.iconRect[i] = new Rect((int) xCenter - w / 2, (int) yCenter - h / 2, (int) xCenter + w / 2, (int) yCenter + h / 2);

            }

            invalidate();  //re-draws the picture
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {

        int state = e.getAction();
        int eventX = (int) e.getX();
        int eventY = (int) e.getY();
        if (state == MotionEvent.ACTION_DOWN) {

            inWedge = false;
            inWedge2 = false;
            inCircle = false;


            //Checks if a pie slice is selected in first Wedge
            for (int i = 0; i < Wedges.length; i++) {

                Wedge f = Wedges[i];
                double slice = (2 * Math.PI) / wedgeQty;
                double start = (2 * Math.PI) * (0.75) - (slice / 2);        //this is done so top slice is the centered on top of the circle

                inWedge = pntInWedge(eventX, eventY,
                        xPosition, yPosition,
                        MinSize, MaxSize,
                        (i * slice) + start, slice);

                if (inWedge == true) {
                    selected = f;
                    break;
                }
            }



            //Checks if center circle is selected
            inCircle = pntInCircle(eventX, eventY, xPosition, yPosition, cRadius);

        } else if (state == MotionEvent.ACTION_UP) {

            if (inCircle) {
                if (Wedge2Shown) {
                    enabled = null;
                    animateOuterIn = true;  //sets Wedge2Shown = false;
                }
                selected = null;

                centerCircle.menuActiviated();

            } else if (selected != null) {
                for (int i = 0; i < Wedges.length; i++) {
                    Wedge f = Wedges[i];
                    if (f == selected) {


                        Toast.makeText(getContext(), menuEntries.get(i).getLabel() + " is clicked.", Toast.LENGTH_SHORT).show();
                        menuEntries.get(i).menuActiviated();

                        RxBus.send("show_week_view");

                        selected = null;
                    }
                }
            } else if (selected2 != null) {
                for (int i = 0; i < Wedges2.length; i++) {
                    Wedge f = Wedges2[i];
                    if (f == selected2) {

                        animateOuterIn = true;  //sets Wedge2Shown = false;
                        enabled = null;
                        selected = null;
                    }
                }
            } else {


                //This is getting called when anything outside the circle is being called
                //Toast.makeText(getContext(), "Area outside rings pressed.", Toast.LENGTH_SHORT).show();

            }
            //selected = null;
            selected2 = null;
            inCircle = false;
        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas c) {


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);

        // draws a dot at the source of the press
        if (showSource == true) {
            paint.setColor(outlineColor);
            paint.setAlpha(outlineAlpha);
            paint.setStyle(Paint.Style.STROKE);
            c.drawCircle(xSource, ySource, cRadius / 10, paint);

            paint.setColor(selectedColor);
            paint.setAlpha(selectedAlpha);
            paint.setStyle(Paint.Style.FILL);
            c.drawCircle(xSource, ySource, cRadius / 10, paint);
        }


        for (int i = 0; i < Wedges.length; i++) {
            Wedge f = Wedges[i];
            paint.setColor(outlineColor);
            paint.setAlpha(outlineAlpha);
            paint.setStyle(Paint.Style.STROKE);
            c.drawPath(f, paint);
            if (f == enabled && Wedge2Shown == true) {
                paint.setColor(wedge2Color);
                paint.setAlpha(wedge2Alpha);
                paint.setStyle(Paint.Style.FILL);
                c.drawPath(f, paint);
            } else if (f != enabled && Wedge2Shown == true) {
                paint.setColor(disabledColor);
                paint.setAlpha(disabledAlpha);
                paint.setStyle(Paint.Style.FILL);
                c.drawPath(f, paint);
            } else if (f == enabled && Wedge2Shown == false) {
                paint.setColor(wedge2Color);
                paint.setAlpha(wedge2Alpha);
                paint.setStyle(Paint.Style.FILL);
                c.drawPath(f, paint);
            } else if (f == selected) {
                paint.setColor(wedge2Color);
                paint.setAlpha(wedge2Alpha);
                paint.setStyle(Paint.Style.FILL);
                c.drawPath(f, paint);
            } else {
                paint.setColor(defaultColor);
                paint.setAlpha(defaultAlpha);
                paint.setStyle(Paint.Style.FILL);
                c.drawPath(f, paint);
            }

            Rect rf = iconRect[i];

           if (menuEntries.get(i).getIcon() != 0) {
                //Puts in the Icon
                Drawable drawable = getResources().getDrawable(menuEntries.get(i).getIcon());
                drawable.setBounds(rf);
                if (f != enabled && Wedge2Shown == true) {
                    drawable.setAlpha(disabledAlpha);
                } else {
                    drawable.setAlpha(pictureAlpha);
                }
                drawable.draw(c);


                //Text Only
            } else {
                //Puts in the Text if no Icon
                paint.setColor(textColor);
                if (f != enabled && Wedge2Shown == true) {
                    paint.setAlpha(disabledAlpha);
                } else {
                    paint.setAlpha(textAlpha);
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(textSize);

                //This will look for a "new line" and split into multiple lines
                String menuItemName = menuEntries.get(i).getLabel();
                String[] stringArray = menuItemName.split("\n");

                //gets total height
                Rect rect = new Rect();
                float textHeight = 0;
                for (int j = 0; j < stringArray.length; j++) {
                    paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rect);
                    textHeight = textHeight + (rect.height() + 3);
                }

                float textBottom = rf.centerY() - (textHeight / 2);
                for (int j = 0; j < stringArray.length; j++) {
                    paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rect);
                    float textLeft = rf.centerX() - rect.width() / 2;
                    textBottom = textBottom + (rect.height() + 3);
                    c.drawText(stringArray[j], textLeft - rect.left, textBottom - rect.bottom, paint);

                }
            }

        }


        if (Wedge2Shown == true) {

            for (int i = 0; i < Wedges2.length; i++) {
                Wedge f = Wedges2[i];
                paint.setColor(outlineColor);
                paint.setAlpha(outlineAlpha);
                paint.setStyle(Paint.Style.STROKE);
                c.drawPath(f, paint);
                if (f == selected2) {
                    paint.setColor(selectedColor);
                    paint.setAlpha(selectedAlpha);
                    paint.setStyle(Paint.Style.FILL);
                    c.drawPath(f, paint);
                } else {
                    paint.setColor(wedge2Color);
                    paint.setAlpha(wedge2Alpha);
                    paint.setStyle(Paint.Style.FILL);
                    c.drawPath(f, paint);
                }

                Rect rf = iconRect2[i];

            }
        }

        //Draws the Middle Circle
        paint.setColor(outlineColor);
        paint.setAlpha(outlineAlpha);
        paint.setStyle(Paint.Style.STROKE);
        c.drawCircle(xPosition, yPosition, cRadius, paint);
        if (inCircle == true) {
            paint.setColor(selectedColor);
            paint.setAlpha(selectedAlpha);
            paint.setStyle(Paint.Style.FILL);
            c.drawCircle(xPosition, yPosition, cRadius, paint);

        } else {
            paint.setColor(defaultColor);
            paint.setAlpha(defaultAlpha);
            paint.setStyle(Paint.Style.FILL);
            c.drawCircle(xPosition, yPosition, cRadius, paint);
        }


        // Draw the circle picture
        if ((centerCircle.getIcon() != 0) && (centerCircle.getLabel() != null)) {

            //This will look for a "new line" and split into multiple lines
            String menuItemName = centerCircle.getLabel();
            String[] stringArray = menuItemName.split("\n");

            paint.setColor(textColor);
            paint.setAlpha(textAlpha);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);

            Rect rectText = new Rect();
            Rect rectIcon = new Rect();
            Drawable drawable = getResources().getDrawable(centerCircle.getIcon());

            int h = getIconSize(drawable.getIntrinsicHeight(), MinIconSize, MaxIconSize);
            int w = getIconSize(drawable.getIntrinsicWidth(), MinIconSize, MaxIconSize);
            rectIcon.set(xPosition - w / 2, yPosition - h / 2, xPosition + w / 2, yPosition + h / 2);


            float textHeight = 0;
            for (int j = 0; j < stringArray.length; j++) {
                paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rectText);
                textHeight = textHeight + (rectText.height() + 3);
            }

            rectIcon.set(rectIcon.left, rectIcon.top - ((int) textHeight / 2), rectIcon.right, rectIcon.bottom - ((int) textHeight / 2));

            float textBottom = rectIcon.bottom;
            for (int j = 0; j < stringArray.length; j++) {
                paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rectText);
                float textLeft = xPosition - rectText.width() / 2;
                textBottom = textBottom + (rectText.height() + 3);
                c.drawText(stringArray[j], textLeft - rectText.left, textBottom - rectText.bottom, paint);
            }


            //Puts in the Icon
            drawable.setBounds(rectIcon);
            drawable.setAlpha(pictureAlpha);
            drawable.draw(c);

            //Icon Only
        } else if (centerCircle.getIcon() != 0) {

            Rect rect = new Rect();

            Drawable drawable = getResources().getDrawable(centerCircle.getIcon());

            int h = getIconSize(drawable.getIntrinsicHeight(), MinIconSize, MaxIconSize);
            int w = getIconSize(drawable.getIntrinsicWidth(), MinIconSize, MaxIconSize);
            rect.set(xPosition - w / 2, yPosition - h / 2, xPosition + w / 2, yPosition + h / 2);

            drawable.setBounds(rect);
            drawable.setAlpha(pictureAlpha);
            drawable.draw(c);

            //Text Only
        } else {
            //Puts in the Text if no Icon
            paint.setColor(textColor);
            paint.setAlpha(textAlpha);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);


            //This will look for a "new line" and split into multiple lines
            String menuItemName = centerCircle.getLabel();
            String[] stringArray = menuItemName.split("\n");

            //gets total height
            Rect rect = new Rect();
            c.rotate(-45, xPosition + rect.exactCenterX(), yPosition + rect.exactCenterY());
            float textHeight = 0;
            for (int j = 0; j < stringArray.length; j++) {
                paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rect);
                textHeight = textHeight + (rect.height() + 3);
            }

            float textBottom = yPosition - (textHeight / 2);
            for (int j = 0; j < stringArray.length; j++) {
                paint.getTextBounds(stringArray[j], 0, stringArray[j].length(), rect);
                float textLeft = xPosition - rect.width() / 2;
                textBottom = textBottom + (rect.height() + 3);
                c.drawText(stringArray[j], textLeft - rect.left, textBottom - rect.bottom, paint);

            }


        }

        // Draws Text in TextBox
        if (headerString != null) {

            paint.setTextSize(headerTextSize);
            paint.getTextBounds(headerString, 0, headerString.length(), this.textRect);
//            if (HeaderBoxBounded == false) {
//                HeaderBoxBounded = true;
//            }

            paint.setColor(outlineColor);
            paint.setAlpha(outlineAlpha);
            paint.setStyle(Paint.Style.STROKE);
            c.drawRoundRect(this.textBoxRect, scalePX(5), scalePX(5), paint);
            paint.setColor(headerBackgroundColor);
            paint.setAlpha(headerBackgroundAlpha);
            paint.setStyle(Paint.Style.FILL);
            c.drawRoundRect(this.textBoxRect, scalePX(5), scalePX(5), paint);

            paint.setColor(headerTextColor);
            paint.setAlpha(headerTextAlpha);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(headerTextSize);
            c.drawText(headerString, headerTextLeft, headerTextBottom, paint);
        }

    }


    public boolean addMenuEntry(DayViewEntry entry) {
        menuEntries.add(entry);
        determineWedges();
        return true;
    }

    public boolean setCenterCircle(DayViewEntry entry) {
        centerCircle = entry;
        return true;
    }

    private boolean pntInCircle(double px, double py, double x1, double y1, double radius) {
        double diffX = x1 - px;
        double diffY = y1 - py;
        double dist = diffX * diffX + diffY * diffY;
        if (dist < radius * radius) {
            return true;
        } else {
            return false;
        }
    }

    private boolean pntInWedge(double px, double py,
                               float xRadiusCenter, float yRadiusCenter,
                               int innerRadius, int outerRadius,
                               double startAngle, double sweepAngle) {
        double diffX = px - xRadiusCenter;
        double diffY = py - yRadiusCenter;

        double angle = Math.atan2(diffY, diffX);
        if (angle < 0)
            angle += (2 * Math.PI);

        if (startAngle >= (2 * Math.PI)) {
            startAngle = startAngle - (2 * Math.PI);
        }

        //checks if point falls between the start and end of the wedge
        if ((angle >= startAngle && angle <= startAngle + sweepAngle) ||
                (angle + (2 * Math.PI) >= startAngle && (angle + (2 * Math.PI)) <= startAngle + sweepAngle)) {

            // checks if point falls inside the radius of the wedge
            double dist = diffX * diffX + diffY * diffY;
            if (dist < outerRadius * outerRadius && dist > innerRadius * innerRadius) {
                return true;
            }
        }
        return false;
    }


    private int getIconSize(int iconSize, int minSize, int maxSize) {

        if (iconSize > minSize) {
            if (iconSize > maxSize) {
                return maxSize;
            } else {    //iconSize < maxSize
                return iconSize;
            }
        } else {  //iconSize < minSize
            return minSize;
        }

    }

    public void setCenterLocation(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        determineWedges();
    }

    public void setSourceLocation(int x, int y) {
        this.xSource = x;
        this.ySource = y;
    }

    public void setShowSourceLocation(boolean showSourceLocation) {
        this.showSource = showSourceLocation;
    }

    private int scalePX(int dp_size) {
        int px_size = (int) (dp_size * screen_density + 0.5f);
        return px_size;
    }


    public class Wedge extends Path {
        private int x, y;
        private int InnerSize, OuterSize;
        private float StartArc;
        private float ArcWidth;

        private Wedge(int x, int y, int InnerSize, int OuterSize, float StartArc, float ArcWidth) {
            super();

            if (StartArc >= 360) {
                StartArc = StartArc - 360;
            }

            this.x = x;
            this.y = y;
            this.InnerSize = InnerSize;
            this.OuterSize = OuterSize;
            this.StartArc = StartArc;
            this.ArcWidth = ArcWidth;
            this.buildPath();
        }

        private void buildPath() {

            final RectF rect = new RectF();
            final RectF rect2 = new RectF();

            //Rectangles values
            rect.set(this.x - this.InnerSize, this.y - this.InnerSize, this.x + this.InnerSize, this.y + this.InnerSize);
            rect2.set(this.x - this.OuterSize, this.y - this.OuterSize, this.x + this.OuterSize, this.y + this.OuterSize);


            this.reset();
            //this.moveTo(100, 100);
            this.arcTo(rect2, StartArc, ArcWidth);
            this.arcTo(rect, StartArc + ArcWidth, -ArcWidth);


            this.close();
        }
    }
}
