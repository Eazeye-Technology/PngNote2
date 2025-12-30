package com.txkj.drawingapp.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.slider.Slider;
import com.txkj.drawingapp.R;

public class BookActivity4FragmentBottom1 {
    private Slider slider;
    private BookActivity4Fragment mFragment;
    public BookActivity4FragmentBottom1(BookActivity4Fragment fragment) {
        this.mFragment = fragment;
    }

    public void init001(View rootView) {
        for (int id : iconsBottomMenu1) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBottomMenu1(rootView, id, true);
                }
            });
        }
        slider = (Slider) rootView.findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mFragment.setSize(mFragment.canvas, value);
            }
        });
    }

    public void init002(View rootView) {
        for (int id : iconsBottomMenu2) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBottomMenu2(rootView, id, true);
                }
            });
        }
        rootView.findViewById(R.id.btnBold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnBold, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnItalics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnItalics, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnUnderline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnUnderline, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnFormatClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnFormatClear, false, true, true);
            }
        });
        rootView.findViewById(R.id.btnAlignLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnAlignLeft, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnAlignCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnAlignCenter, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnAlignRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnAlignRight, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnAlignJustify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnAlignJustify, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnBullet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnBullet, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnNumber, true, false, false);
            }
        });
        rootView.findViewById(R.id.btnStyleHand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnStyleHand, true, false, false);
            }
        });
        rootView.findViewById(R.id.btnSerif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnSerif, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnSans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnSans, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnTitle, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnH1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnH1, true, false, false);
            }
        });
        rootView.findViewById(R.id.btnH2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnH2, true, false, true);
            }
        });
        rootView.findViewById(R.id.btnH3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(rootView, R.id.btnH3, true, false, true);
            }
        });
    }

    //--------------------------
    private final static int iconsBottomMenu1[] = {
            R.id.colorBtn001,
            R.id.colorBtn002,
            R.id.colorBtn003,
            R.id.colorBtn004,
            R.id.colorBtn005,
            R.id.colorBtn006,
            R.id.colorBtn007,
            R.id.colorBtn008,
            R.id.colorBtn009,
    };
    private final static int iconsBottomMenu2[] = {
            R.id.colorBtn101,
            R.id.colorBtn102,
            R.id.colorBtn103,
            R.id.colorBtn104,
            R.id.colorBtn105,
            R.id.colorBtn106,
            R.id.colorBtn107,
            R.id.colorBtn108,
            R.id.colorBtn109,
    };
    private void onClickBottomMenu1(View rootView, int id, boolean isClick) {
        View view = rootView.findViewById(id);
        for (int i = 0; i < iconsBottomMenu1.length; ++i) {
            View viewIcon = rootView.findViewById(iconsBottomMenu1[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0xFFF1EDEC); //白色背景
                //((CardView) viewIcon).setCardElevation(0.0f);
//                if (viewIcon.findViewWithTag("binding_1") != null) {
//                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
//                            getActiveIconIdSubmenu1(iconsSubmenu1[i], false));
//                }
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((TextView) viewIcon.findViewWithTag("binding_1")).setTextColor(0xFF000000); //黑色字
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((TextView) view.findViewWithTag("binding_1")).setTextColor(0xFFFFFFFF); //白色字
            }
        }
        int color = Color.BLACK;
        //https://www.cnblogs.com/huojiaoqingchun0123/p/6753642.html
        if (id == R.id.colorBtn001) {
            color = 0xFF000000; //Black //0xFF000000;
        } else if (id == R.id.colorBtn002) {
            color = 0xFF696969; //DimGray //0xFF0000FF;
        } else if (id == R.id.colorBtn003) {
            color = 0xFF808080; //Gray //0xFFFF0000;
        } else if (id == R.id.colorBtn004) {
            color = 0xFFA9A9A9; //DarkGray //0xFF00FF00;
        } else if (id == R.id.colorBtn005) {
            color = 0xFFC0C0C0; //Silver //0xFFFFA500;
        } else if (id == R.id.colorBtn006) {
            color = 0xFFD3D3D3; //LightGray //0xFFFFFF00;
        } else if (id == R.id.colorBtn007) {
            color = 0xFFDCDCDC; //Gainsboro //0xFF800080;
        } else if (id == R.id.colorBtn008) {
            color = 0xFFF5F5F5; //WhiteSmoke//0xFFA52A2A;
        } else if (id == R.id.colorBtn009) {
            color = 0xFFFFFFFF; //White//0xFF808080;
        }
        this.mFragment.setPenColor(color);
    }
    private void onClickBottomMenu2(View rootView, int id, boolean isClick) {
        View view = rootView.findViewById(id);
        for (int i = 0; i < iconsBottomMenu2.length; ++i) {
            View viewIcon = rootView.findViewById(iconsBottomMenu2[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0xFFF1EDEC); //白色背景
                //((CardView) viewIcon).setCardElevation(0.0f);
//                if (viewIcon.findViewWithTag("binding_1") != null) {
//                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
//                            getActiveIconIdSubmenu1(iconsSubmenu1[i], false));
//                }
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((TextView) viewIcon.findViewWithTag("binding_1")).setTextColor(0xFF000000); //黑色字
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((TextView) view.findViewWithTag("binding_1")).setTextColor(0xFFFFFFFF); //白色字
            }
        }
        int color = Color.BLACK;
        if (id == R.id.colorBtn101) {
            color = 0xFF000000; //Black //0xFF000000;
        } else if (id == R.id.colorBtn102) {
            color = 0xFF696969; //DimGray //0xFF0000FF;
        } else if (id == R.id.colorBtn103) {
            color = 0xFF808080; //Gray //0xFFFF0000;
        } else if (id == R.id.colorBtn104) {
            color = 0xFFA9A9A9; //DarkGray //0xFF00FF00;
        } else if (id == R.id.colorBtn105) {
            color = 0xFFC0C0C0; //Silver //0xFFFFA500;
        } else if (id == R.id.colorBtn106) {
            color = 0xFFD3D3D3; //LightGray //0xFFFFFF00;
        } else if (id == R.id.colorBtn107) {
            color = 0xFFDCDCDC; //Gainsboro //0xFF800080;
        } else if (id == R.id.colorBtn108) {
            color = 0xFFF5F5F5; //WhiteSmoke//0xFFA52A2A;
        } else if (id == R.id.colorBtn109) {
            color = 0xFFFFFFFF; //White//0xFF808080;
        }
        //setPenColor(color);
        this.mFragment.setEditTextColor(color);
    }

    private void onClickBottomButton1(View rootView, int id, boolean isToggle, boolean isClear, boolean isClick) {
        if (id == R.id.btnBold) {
            if (isToggle) {
                this.mFragment.setBold(!this.mFragment.isBold);
            } else if (isClear) {
                this.mFragment.setBold(false);
            }
        } else if (id == R.id.btnItalics) {
            if (isToggle) {
                this.mFragment.setItalics(!this.mFragment.isItalics);
            } else if (isClear) {
                this.mFragment.setItalics(false);
            }
        } else if (id == R.id.btnUnderline) {
            if (isToggle) {
                this.mFragment.setUnderline(!this.mFragment.isUnderline);
            } else {
                this.mFragment.setUnderline(false);
            }
        } else if (id == R.id.btnFormatClear) {
            this.mFragment.setBold(false);
            this.mFragment.setItalics(false);
            this.mFragment.setUnderline(false);
        } else if (id == R.id.btnAlignLeft) {
            if (isToggle) {
                if (this.mFragment.alignType != BookActivity4Fragment.ALIGN_TYPE_LEFT) {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_LEFT);
                } else {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
                }
            } else {
                this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignCenter) {
            if (isToggle) {
                if (this.mFragment.alignType != BookActivity4Fragment.ALIGN_TYPE_CENTER) {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_CENTER);
                } else {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
                }
            } else {
                this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignRight) {
            if (isToggle) {
                if (this.mFragment.alignType != BookActivity4Fragment.ALIGN_TYPE_RIGHT) {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_RIGHT);
                } else {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
                }
            } else {
                this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignJustify) {
            if (isToggle) {
                if (this.mFragment.alignType != BookActivity4Fragment.ALIGN_TYPE_JUSTIFY) {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_JUSTIFY);
                } else {
                    this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
                }
            } else {
                this.mFragment.setAlignType(BookActivity4Fragment.ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnBullet) {
            if (isToggle) {
                if (this.mFragment.listType != BookActivity4Fragment.LIST_TYPE_BULLET) {
                    this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_BULLET);
                } else {
                    this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_NONE);
                }
            } else {
                this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_NONE);
            }
        } else if (id == R.id.btnNumber) {
            if (isToggle) {
                if (this.mFragment.listType != BookActivity4Fragment.LIST_TYPE_NUMBER) {
                    this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_NUMBER);
                } else {
                    this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_NONE);
                }
            } else {
                this.mFragment.setListType(BookActivity4Fragment.LIST_TYPE_NONE);
            }
        } else if (id == R.id.btnStyleHand) {
            if (isToggle) {
                if (this.mFragment.styleType != BookActivity4Fragment.STYLE_TYPE_HAND) {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_HAND);
                } else {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
                }
            } else {
                this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnSerif) {
            if (isToggle) {
                if (this.mFragment.styleType != BookActivity4Fragment.STYLE_TYPE_SERIF) {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_SERIF);
                } else {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
                }
            } else {
                this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnSans) {
            if (isToggle) {
                if (this.mFragment.styleType != BookActivity4Fragment.STYLE_TYPE_SANS) {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_SANS);
                } else {
                    this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
                }
            } else {
                this.mFragment.setStyleType(BookActivity4Fragment.STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnTitle) {
            if (isToggle) {
                if (this.mFragment.sizeType != BookActivity4Fragment.SIZE_TYPE_TITLE) {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_TITLE);
                } else {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
                }
            } else {
                this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH1) {
            if (isToggle) {
                if (this.mFragment.sizeType != BookActivity4Fragment.SIZE_TYPE_H1) {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_H1);
                } else {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
                }
            } else {
                this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH2) {
            if (isToggle) {
                if (this.mFragment.sizeType != BookActivity4Fragment.SIZE_TYPE_H2) {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_H2);
                } else {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
                }
            } else {
                this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH3) {
            if (isToggle) {
                if (this.mFragment.sizeType != BookActivity4Fragment.SIZE_TYPE_H3) {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_H3);
                } else {
                    this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
                }
            } else {
                this.mFragment.setSizeType(BookActivity4Fragment.SIZE_TYPE_NONE);
            }
        }
        CardView viewCard_bold = (CardView) rootView.findViewById(R.id.btnBold);
        ImageView icon_bold = (ImageView) viewCard_bold.findViewWithTag("icon");
        CardView viewCard_italics = (CardView) rootView.findViewById(R.id.btnItalics);
        ImageView icon_italics = (ImageView) viewCard_italics.findViewWithTag("icon");
        CardView viewCard_underline = (CardView) rootView.findViewById(R.id.btnUnderline);
        ImageView icon_underline = (ImageView) viewCard_underline.findViewWithTag("icon");
        if (this.mFragment.isBold) {
            viewCard_bold.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_bold.setImageResource(R.drawable.ic_my_para_bold_011_w);
        } else {
            viewCard_bold.setCardBackgroundColor(0x00585858);
            icon_bold.setImageResource(R.drawable.ic_my_para_bold_011);
        }
        if (this.mFragment.isItalics) {
            viewCard_italics.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_italics.setImageResource(R.drawable.ic_my_para_italics_012_w);
        } else {
            viewCard_italics.setCardBackgroundColor(0x00585858);
            icon_italics.setImageResource(R.drawable.ic_my_para_italics_012);
        }
        if (this.mFragment.isUnderline) {
            viewCard_underline.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_underline.setImageResource(R.drawable.ic_my_para_underline_013_w);
        } else {
            viewCard_underline.setCardBackgroundColor(0x00585858);
            icon_underline.setImageResource(R.drawable.ic_my_para_underline_013);
        }
        CardView viewCard_alignLeft = (CardView) rootView.findViewById(R.id.btnAlignLeft);
        ImageView icon_alignLeft = (ImageView) viewCard_alignLeft.findViewWithTag("icon");
        CardView viewCard_alignCenter = (CardView) rootView.findViewById(R.id.btnAlignCenter);
        ImageView icon_alignCenter = (ImageView) viewCard_alignCenter.findViewWithTag("icon");
        CardView viewCard_alignRight = (CardView) rootView.findViewById(R.id.btnAlignRight);
        ImageView icon_alignRight = (ImageView) viewCard_alignRight.findViewWithTag("icon");
        CardView viewCard_justify = (CardView) rootView.findViewById(R.id.btnAlignJustify);
        ImageView icon_justify = (ImageView) viewCard_justify.findViewWithTag("icon");
        if (this.mFragment.alignType == BookActivity4Fragment.ALIGN_TYPE_LEFT) {
            viewCard_alignLeft.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignLeft.setImageResource(R.drawable.ic_my_para_left_015_w);
        } else {
            viewCard_alignLeft.setCardBackgroundColor(0x00585858);
            icon_alignLeft.setImageResource(R.drawable.ic_my_para_left_015);
        }
        if (this.mFragment.alignType == BookActivity4Fragment.ALIGN_TYPE_CENTER) {
            viewCard_alignCenter.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignCenter.setImageResource(R.drawable.ic_my_para_center_017_w);
        } else {
            viewCard_alignCenter.setCardBackgroundColor(0x00585858);
            icon_alignCenter.setImageResource(R.drawable.ic_my_para_center_017);
        }
        if (this.mFragment.alignType == BookActivity4Fragment.ALIGN_TYPE_RIGHT) {
            viewCard_alignRight.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignRight.setImageResource(R.drawable.ic_my_para_right_016_w);
        } else {
            viewCard_alignRight.setCardBackgroundColor(0x00585858);
            icon_alignRight.setImageResource(R.drawable.ic_my_para_right_016);
        }
        if (this.mFragment.alignType == BookActivity4Fragment.ALIGN_TYPE_JUSTIFY) {
            viewCard_justify.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_justify.setImageResource(R.drawable.ic_my_para_justify_018_w);
        } else {
            viewCard_justify.setCardBackgroundColor(0x00585858);
            icon_justify.setImageResource(R.drawable.ic_my_para_justify_018);
        }
        CardView viewCard_bullet = (CardView) rootView.findViewById(R.id.btnBullet);
        ImageView icon_bullet = (ImageView) viewCard_bullet.findViewWithTag("icon");
        CardView viewCard_number = (CardView) rootView.findViewById(R.id.btnNumber);
        ImageView icon_number = (ImageView) viewCard_number.findViewWithTag("icon");
        if (this.mFragment.listType == BookActivity4Fragment.LIST_TYPE_BULLET) {
            viewCard_bullet.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_bullet.setImageResource(R.drawable.ic_my_para_bullet_019_w);
        } else {
            viewCard_bullet.setCardBackgroundColor(0x00585858);
            icon_bullet.setImageResource(R.drawable.ic_my_para_bullet_019);
        }
        if (this.mFragment.listType == BookActivity4Fragment.LIST_TYPE_NUMBER) {
            viewCard_number.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_number.setImageResource(R.drawable.ic_my_para_number_020_w);
        } else {
            viewCard_number.setCardBackgroundColor(0x00585858);
            icon_number.setImageResource(R.drawable.ic_my_para_number_020);
        }
        CardView viewCard_hand = (CardView) rootView.findViewById(R.id.btnStyleHand);
        ImageView icon_hand = (ImageView) viewCard_hand.findViewWithTag("icon");
        CardView viewCard_serif = (CardView) rootView.findViewById(R.id.btnSerif);
        ImageView icon_serif = (ImageView) viewCard_serif.findViewWithTag("icon");
        CardView viewCard_sans = (CardView) rootView.findViewById(R.id.btnSans);
        ImageView icon_sans = (ImageView) viewCard_sans.findViewWithTag("icon");
        if (this.mFragment.styleType == BookActivity4Fragment.STYLE_TYPE_HAND) {
            viewCard_hand.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_hand.setImageResource(R.drawable.ic_my_style_hand_005_w);
        } else {
            viewCard_hand.setCardBackgroundColor(0x00585858);
            icon_hand.setImageResource(R.drawable.ic_my_style_hand_005);
        }
        if (this.mFragment.styleType == BookActivity4Fragment.STYLE_TYPE_SERIF) {
            viewCard_serif.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_serif.setImageResource(R.drawable.ic_my_style_serif_006_w);
        } else {
            viewCard_serif.setCardBackgroundColor(0x00585858);
            icon_serif.setImageResource(R.drawable.ic_my_style_serif_006);
        }
        if (this.mFragment.styleType == BookActivity4Fragment.STYLE_TYPE_SANS) {
            viewCard_sans.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_sans.setImageResource(R.drawable.ic_my_style_sans_007_w);
        } else {
            viewCard_sans.setCardBackgroundColor(0x00585858);
            icon_sans.setImageResource(R.drawable.ic_my_style_sans_007);
        }
        CardView viewCard_title = (CardView) rootView.findViewById(R.id.btnTitle);
        ImageView icon_title = (ImageView) viewCard_title.findViewWithTag("icon");
        CardView viewCard_h1 = (CardView) rootView.findViewById(R.id.btnH1);
        ImageView icon_h1 = (ImageView) viewCard_h1.findViewWithTag("icon");
        CardView viewCard_h2 = (CardView) rootView.findViewById(R.id.btnH2);
        ImageView icon_h2 = (ImageView) viewCard_h2.findViewWithTag("icon");
        CardView viewCard_h3 = (CardView) rootView.findViewById(R.id.btnH3);
        ImageView icon_h3 = (ImageView) viewCard_h3.findViewWithTag("icon");
        if (this.mFragment.sizeType == BookActivity4Fragment.SIZE_TYPE_TITLE) {
            viewCard_title.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_title.setImageResource(R.drawable.ic_my_style_title_001_w);
        } else {
            viewCard_title.setCardBackgroundColor(0x00585858);
            icon_title.setImageResource(R.drawable.ic_my_style_title_001);
        }
        if (this.mFragment.sizeType == BookActivity4Fragment.SIZE_TYPE_H1) {
            viewCard_h1.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h1.setImageResource(R.drawable.ic_my_style_h1_002_w);
        } else {
            viewCard_h1.setCardBackgroundColor(0x00585858);
            icon_h1.setImageResource(R.drawable.ic_my_style_h1_002);
        }
        if (this.mFragment.sizeType == BookActivity4Fragment.SIZE_TYPE_H2) {
            viewCard_h2.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h2.setImageResource(R.drawable.ic_my_style_h2_003_w);
        } else {
            viewCard_h2.setCardBackgroundColor(0x00585858);
            icon_h2.setImageResource(R.drawable.ic_my_style_h2_003);
        }
        if (this.mFragment.sizeType == BookActivity4Fragment.SIZE_TYPE_H3) {
            viewCard_h3.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h3.setImageResource(R.drawable.ic_my_style_h3_004_w);
        } else {
            viewCard_h3.setCardBackgroundColor(0x00585858);
            icon_h3.setImageResource(R.drawable.ic_my_style_h3_004);
        }
    }
}
