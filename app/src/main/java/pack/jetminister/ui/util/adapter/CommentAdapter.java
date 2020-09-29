package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Comment;
import pack.jetminister.data.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    public class CommentHolder extends RecyclerView.ViewHolder {
        private TextView usernameComment , commentComment;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            usernameComment = itemView.findViewById(R.id.username_comment_tv);
            commentComment = itemView.findViewById(R.id.comment_comment_tv);
        }
    }

    private Context mContext;
    private List<Comment> mCommentList;

    public CommentAdapter(Context context, List<Comment> comments){
        mContext = context;
        mCommentList = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_comment, parent, false);


        return new CommentAdapter.CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        Comment uploadCurrent = mCommentList.get(position);
        holder.usernameComment.setText("" + uploadCurrent.getUsername() + " :");
        holder.commentComment.setText(uploadCurrent.getBody());

        holder.usernameComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userUID  = uploadCurrent.getUserID();
                //TODO make a connection to a profile page
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

}
