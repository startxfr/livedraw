package fr.startx.livedraw;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.startx.livedraw.models.Paint;
import fr.startx.livedraw.view.adapter.PaintAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final PaintAdapter adapter = new PaintAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paint p = new Paint("New Paint");
                ObjectMapper mapper = new ObjectMapper();
                MutableDocument mDoc = new MutableDocument(p.getId());

                adapter.addData(p, 0);
                recyclerView.smoothScrollToPosition(0);

                HashMap<String, Object> map = mapper.convertValue(p, new TypeReference<Map<String, Object>>() {});
                mDoc.setData(map);
                DatabaseManager.createDocument(mDoc);

                Snackbar.make(view, "Document added", Snackbar.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add animation duration
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(300);
        recyclerView.setItemAnimator(itemAnimator);

        Query query = QueryBuilder.select(
                SelectResult.all()
        ).from(
                DataSource.database(DatabaseManager.getDatabase())
        ).where(Expression.property("type").equalTo(Expression.string(Paint.TYPE)))
                .orderBy(Ordering.property("created_at").descending());

        query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(@NonNull QueryChange change) {
                ResultSet resultSet = change.getResults();
                ObjectMapper mapper = new ObjectMapper();
                List<Paint> data = new ArrayList<>();

                for (Result result : resultSet) {
                    Dictionary r = result.getDictionary(DatabaseManager.DB_NAME);

                    Paint p = mapper.convertValue(r.toMap(), Paint.class);
                    data.add(p);
                }

                adapter.setData(data);

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
