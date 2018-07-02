package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.widget.ExpandableListView

class SecondLevelExpandableListView(context: Context): ExpandableListView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasureSpec2 = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }
}