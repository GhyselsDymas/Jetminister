package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Comment;
import pack.jetminister.ui.activities.StreamerProfileActivity;

import static pack.jetminister.data.User.KEY_USER_ID;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context mContext;
    private List<Comment> mCommentList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CommentAdapter(Context context, List<Comment> comments) {
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

        Comment currentComment = mCommentList.get(position);
        String commentUID = currentComment.getUserID();
        holder.usernameComment.setText(currentComment.getUsername());
        holder.commentComment.setText(" : " + currentComment.getBody());
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid().equals(commentUID)) {
            holder.usernameComment.setClickable(false);
        } else {
            holder.usernameComment.setPaintFlags(holder.usernameComment.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.usernameComment.setClickable(true);
            holder.usernameComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), StreamerProfileActivity.class);
                    intent.putExtra(KEY_USER_ID, commentUID);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        private TextView usernameComment, commentComment;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            usernameComment = itemView.findViewById(R.id.comment_tv_username);
            commentComment = itemView.findViewById(R.id.comment_tv_body);
        }
    }


}
