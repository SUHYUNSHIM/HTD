package com.example.hows_this_day
//출처:https://woochan-dev.tistory.com/27
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

open class ViewHolderHelper(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
//리사이클뷰 뷰홀더 도와주는 클래스