## TabLayoutIndicator
自定义tablayout的指示器，实现粘连效果

## Usage
 add dependency:
```
compile 'com.cheng:ViewPagerIndicator:1.0.3'
```

## 可以自定义indicator如下属性
 
- **width ：固定宽度**
- **color：颜色**
- **drawable：设置了drawable，color属性失效**
- **can_anim: 是否实现粘连动画：默认false**
- **match_title_width: 在can_anim=true的情况下，是否根据title的宽度自动变化为对应title的宽度，如例子中第3例和第4例**
- **scale ：match_title_width=true的情况下，是否动态调整宽度比例。当match_title_width=true时，如果发现宽度与文字不符合时，可以调整这个参数，动态调整**

## 注意事项
**必须在viewPager设置了adapter之后，再将viewpager设置给indicator**

## 例子如下：
```

    private fun initViewPager() {
        viewPageAdapter.fragmentPages.addAll(getFragmentPages())
        viewPager.adapter = viewPageAdapter
        tabLayout.setupWithViewPager(viewPager)
       
        //再将viewpager设置给indicator
        tabLayoutIndicator1.mViewPager = viewPager
        tabLayoutIndicator2.mViewPager = viewPager
        tabLayoutIndicator3.mViewPager = viewPager
        tabLayoutIndicator4.mViewPager = viewPager
    }

    private fun getFragmentPages(): List<FragmentPage> =
        listOf(
            FragmentPage(PageFragment(), "Followers"),
            FragmentPage(PageFragment(), "关注"),
            FragmentPage(PageFragment(), "me"),
            FragmentPage(PageFragment(), "测试标题")
        )
```
 ![img](https://github.com/chenguo4930/TabLayoutIndicator/blob/master/indicator.gif)
 
 ```
    <!--自定义TabLayout指示器-->
    <declare-styleable name="TabLayoutIndicator">
        <attr name="indicator_width" format="dimension|reference" />
        <attr name="indicator_color" format="color|reference" />
        <attr name="indicator_drawable" format="reference" />
        <attr name="indicator_can_anim" format="boolean" />
        <attr name="indicator_match_title_width" format="boolean" />
        <attr name="indicator_scale" format="float" />
    </declare-styleable>
```

```
<!--固定宽度、设置颜色-->
com.chengguo.indicator.TabLayoutIndicator
            android:id="@+id/tabLayoutIndicator1"
            android:background="#ffffff"
            android:layout_width="match_parent"
            android:layout_height="6dp"
            app:indicator_with="50dp"
            app:indicator_color="#ff00ff"/>

<!--开启粘性动画、固定宽度、设置drawable-->
    <com.chengguo.indicator.TabLayoutIndicator
            android:id="@+id/tabLayoutIndicator2"
            android:layout_marginTop="10dp"
            android:background="#ffffff"
            android:layout_width="match_parent"
            android:layout_height="6dp"
            app:indicator_can_anim="true"
            app:indicator_with="50dp"
            app:indicator_drawable="@drawable/indicator_shape"/>

<!--开启粘性动画、动态匹配title宽度、设置颜色-->
    <com.chengguo.indicator.TabLayoutIndicator
            android:id="@+id/tabLayoutIndicator3"
            android:background="#ffffff"
            android:layout_marginTop="10dp"
            android:layout_width="match_parent"
            android:layout_height="6dp"
            app:indicator_can_anim="true"
            app:indicator_match_title_width="true"
            app:indicator_color="#2741e9"/>

<!--开启粘性动画、动态匹配title宽度、设置颜色、动态调整比例因子-->
    <com.chengguo.indicator.TabLayoutIndicator
            android:id="@+id/tabLayoutIndicator4"
            android:background="#ffffff"
            android:layout_marginTop="10dp"
            android:layout_width="match_parent"
            android:layout_height="6dp"
            app:indicator_can_anim="true"
            app:indicator_match_title_width="true"
            app:indicator_color="#0c0c0e"
            app:indicator_scale="4.0"/>
```

