package Avange.apptip;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import static Avange.apptip.CustomProgress.customProgress;
import static Avange.apptip.Variables.context;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import Avange.apptip.plugin.CommentManager;
import Avange.apptip.plugin.SourcePositionListener;
import Avange.apptip.plugin.UndoRedoManager;
import Avange.apptip.syntax.LanguageManager;
import Avange.apptip.syntax.LanguageName;
import Avange.apptip.syntax.ThemeName;
//Clase para llenar los tips en mainactivity
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersAdapterVh> implements Filterable {
    public LanguageManager languageManager;
    public CommentManager commentManager;
    public UndoRedoManager undoRedoManager;
    public TextView languageNameText;
    public TextView sourcePositionText;
    public LanguageName currentLanguage = LanguageName.JAVA;
    public ThemeName currentTheme = ThemeName.MONOKAI;

    public CodeView codeView;
    public final boolean useModernAutoCompleteAdapter = true;
    private List<UserModel> userModelList;
    private List<UserModel> getUserModelListFiltered;
    private Context context;
    private SelectedUser selectedUser;
    ArrayList<Integer> myarray = Variables.ramdomizeArray();
    Random r = new Random();
    public UsersAdapter(List<UserModel> userModelList, SelectedUser selectedUser) {
        this.userModelList = userModelList;
        this.getUserModelListFiltered = userModelList;
        this.selectedUser = selectedUser;
    }

    @NonNull
    @Override
    public UsersAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        return new UsersAdapterVh(LayoutInflater.from(context).inflate(R.layout.row_users,null));
    }
    //En este método seteamos todos los controles del tip
    @Override
    public void onBindViewHolder(@NonNull UsersAdapterVh holder, int position) {
        if(myarray.size()==0) myarray=Variables.ramdomizeArray();
        if(myarray.size()==5) Collections.shuffle(myarray);
        Drawable drawable = getDrawable(Variables.context, myarray.get(0));
        myarray.remove(0);
        holder.carpeta.setBackground(drawable);
        UserModel userModel = userModelList.get(position);
        String prefix = userModel.getUserName().substring(0,1).toUpperCase();
        holder.tvUsername.setText(userModel.getTitle());
        holder.tvPrefix.setText(prefix);
        holder.codeView.setText(userModel.getCodigo());
        holder.codeViewResena.setText(userModel.getResena());
        holder.likesbutton.setTag(userModel.getIdtip()+";"+userModel.getiduser());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }
    //Aplicación del filtro para los Tips
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence == null | charSequence.length() == 0){
                    filterResults.count = getUserModelListFiltered.size();
                    filterResults.values = getUserModelListFiltered;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<UserModel> resultData = new ArrayList<>();

                    for(UserModel userModel: getUserModelListFiltered){
                        if(userModel.getUserName().toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                userModelList = (List<UserModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }


    public interface SelectedUser{

        void selectedUser(UserModel userModel);
        void selectedText(String text);

    }
    //Usando el viewholder para mostrar los controles en el mainactivity
    public class UsersAdapterVh extends RecyclerView.ViewHolder {
        RelativeLayout carpeta;
        TextView tvPrefix;
        TextView tvUsername;
        TextView languageNameText;
        LinearLayout likesbutton;
        ImageButton imagelike;
        TextView numLikes;
        long numOfLikes = 0;
        public CodeView codeView,codeViewResena;
            public UsersAdapterVh(@NonNull View itemView) {
            super(itemView);
            tvPrefix = itemView.findViewById(R.id.prefix);
            tvUsername = itemView.findViewById(R.id.titulo);
            carpeta = itemView.findViewById(R.id.carpeta);
            codeView = itemView.findViewById(R.id.detallemain);
            codeViewResena = itemView.findViewById(R.id.codeViewmain2);
            languageNameText = itemView.findViewById(R.id.language_name_txt);
            Variables.languageNameText = languageNameText;
            sourcePositionText = itemView.findViewById(R.id.source_position_txt);
            likesbutton = itemView.findViewById(R.id.likesbutton);
            imagelike = itemView.findViewById(R.id.imageButton9);
            numLikes = itemView.findViewById(R.id.textView6);
                likesbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // si no esta autenticado  muestra la pantalla de atutenticación
                    try {
                        if(!Variables.logging){
                            Toast.makeText(context,"Tiene que autenticarse para dar Like",Toast.LENGTH_SHORT).show();
                            startActivity(context,new Intent(context,MainActivity3.class),null);
                        }
                        else {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            if(!Variables.host.equals("0.0.0.0")) database.useEmulator(Variables.host, 9015);
                            DatabaseReference mDatabase = database.getReference();
                            String [] data = view.getTag().toString().split(";");
                            if(Variables.logging){
                                DB db = new DB();
                                db.storeLikes(context,data[0],data[1],mDatabase,displayNumberOfLikes(data[0],data[1]));
                                imagelike.setImageResource(R.drawable.likeactive);
                            }
                        }
                    }catch (Exception e){
                        String exp = e.getStackTrace().toString();
                    }
                }
            });
            codeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedUser.selectedText("Código;"+codeView.getText().toString());
                }
            });
            codeViewResena.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedUser.selectedText("Reseña;"+codeViewResena.getText().toString());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
                codeView.setTextIsSelectable(true);
                codeView.setKeyListener(null);
                codeViewResena.setTextIsSelectable(true);
                codeViewResena.setKeyListener(null);
            configCodeView(codeView);
            configCodeViewPlugins(codeView, Variables.languageNameText);
        }
        public long displayNumberOfLikes(String idtip, String iduser){
            numOfLikes = 0;
            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes").child(idtip);
            likesRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.hasChild("like")){
                            numOfLikes = dataSnapshot.child("like").getValue(Long.class);
                        }
                        //This is to check if the user has liked the post or not
                        numLikes.setText(String.valueOf(numOfLikes));
                        if(dataSnapshot.hasChild(iduser))
                        imagelike.setImageResource(R.drawable.likeactive);
                        else {
                            imagelike.setImageResource(R.drawable.likeunactive);
                            DB db = new DB();
                            db.removeLikes(iduser,likesRef);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
            return numOfLikes;
        }
    }

    private void configCodeView(CodeView codeView) {
        try{
        // Change default font to JetBrains Mono font
        Typeface jetBrainsMono = ResourcesCompat.getFont(context.getApplicationContext(), R.font.jetbrains_mono_medium);

        codeView.setTypeface(jetBrainsMono);

        // Setup Line number feature
        codeView.setEnableLineNumber(true);
        codeView.setLineNumberTextColor(Color.GRAY);
        codeView.setLineNumberTextSize(25f);

        // Setup highlighting current line
        codeView.setEnableHighlightCurrentLine(false);
        codeView.setHighlightCurrentLineColor(Color.GRAY);

        // Setup Auto indenting feature
        codeView.setTabLength(4);
        codeView.setEnableAutoIndentation(true);

        // Setup the language and theme with SyntaxManager helper class
        languageManager = new LanguageManager(context.getApplicationContext(), codeView);
        languageManager.applyTheme(currentLanguage, currentTheme);

        // Setup auto pair complete
        final Map<Character, Character> pairCompleteMap = new HashMap<>();
        pairCompleteMap.put('{', '}');
        pairCompleteMap.put('[', ']');
        pairCompleteMap.put('(', ')');
        pairCompleteMap.put('<', '>');
        pairCompleteMap.put('"', '"');
        pairCompleteMap.put('\'', '\'');

        codeView.setPairCompleteMap(pairCompleteMap);
        codeView.enablePairComplete(true);
        codeView.enablePairCompleteCenterCursor(true);
        }catch (Exception e){
            String exp = e.getStackTrace().toString();
        }

        // Setup the auto complete and auto indenting for the current language
        configLanguageAutoComplete(codeView);
        configLanguageAutoIndentation(codeView);
    }
    private void configLanguageAutoComplete(CodeView codeView) {
        try {
            if (useModernAutoCompleteAdapter) {
                // Load the code list (keywords and snippets) for the current language
                List<Code> codeList = languageManager.getLanguageCodeList(currentLanguage);

                // Use CodeViewAdapter or custom one
                CustomCodeViewAdapter adapter = new CustomCodeViewAdapter(context.getApplicationContext(), codeList);

                // Add the odeViewAdapter to the CodeView
                codeView.setAdapter(adapter);
            } else {
                String[] languageKeywords = languageManager.getLanguageKeywords(currentLanguage);

                // Custom list item xml layout
                final int layoutId = R.layout.list_item_suggestion;

                // TextView id to put suggestion on it
                final int viewId = R.id.suggestItemTextView;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context.getApplicationContext(), layoutId, viewId, languageKeywords);

                // Add the ArrayAdapter to the CodeView
                codeView.setAdapter(adapter);
            }
        }catch (Exception e){
            String exp = e.getStackTrace().toString();
        }
    }

    private void configLanguageAutoIndentation(CodeView codeView) {
        codeView.setIndentationStarts(languageManager.getLanguageIndentationStarts(currentLanguage));
        codeView.setIndentationEnds(languageManager.getLanguageIndentationEnds(currentLanguage));
    }

    private void configCodeViewPlugins(CodeView codeView, TextView languageNameText) {
        commentManager = new CommentManager(codeView);
        configCommentInfo();

        undoRedoManager = new UndoRedoManager(codeView);
        undoRedoManager.connect();

        Context context1 = context.getApplicationContext();
        configLanguageName(languageNameText);
        sourcePositionText.setText(context1.getString(R.string.source_position, 0, 0));
        configSourcePositionListener(codeView);
    }
    private void configLanguageName(TextView languageNameText) {
        languageNameText.setText(currentLanguage.name().toLowerCase());
    }
    private void configCommentInfo() {
        commentManager.setCommentStart(languageManager.getCommentStart(currentLanguage));
        commentManager.setCommendEnd(languageManager.getCommentEnd(currentLanguage));
    }

    private void configSourcePositionListener(CodeView codeView) {
        try{
        Context context1 = context.getApplicationContext();
        SourcePositionListener sourcePositionListener = new SourcePositionListener(codeView);
        sourcePositionListener.setOnPositionChanged((line, column) -> {
            sourcePositionText.setText(context1.getString(R.string.source_position, line, column));
        });} catch (Exception e) {
            String exp = e.getStackTrace().toString();
        }
    }
    private void changeTheEditorLanguage(int languageId) {
        final LanguageName oldLanguage = currentLanguage;
        if (languageId == R.id.language_java) currentLanguage = LanguageName.JAVA;
        else if (languageId == R.id.language_python) currentLanguage = LanguageName.PYTHON;
        else if(languageId == R.id.language_go) currentLanguage = LanguageName.GO_LANG;

        if (currentLanguage != oldLanguage) {
            languageManager.applyTheme(currentLanguage, currentTheme);
            configLanguageName(languageNameText);
            configLanguageAutoComplete(codeView);
            configLanguageAutoIndentation(codeView);
            configCommentInfo();
        }
    }

    private void changeTheEditorTheme(int themeId) {
        final ThemeName oldTheme = currentTheme;
        if (themeId == R.id.theme_monokia) currentTheme = ThemeName.MONOKAI;
        else if (themeId == R.id.theme_noctics) currentTheme = ThemeName.NOCTIS_WHITE;
        else if(themeId == R.id.theme_five_color) currentTheme = ThemeName.FIVE_COLOR;
        else if(themeId == R.id.theme_orange_box) currentTheme = ThemeName.ORANGE_BOX;

        if (currentTheme != oldTheme) {
            languageManager.applyTheme(currentLanguage, currentTheme);
        }
    }

    private void toggleRelativeLineNumber() {
        boolean isRelativeLineNumberEnabled = codeView.isLineRelativeNumberEnabled();
        isRelativeLineNumberEnabled = !isRelativeLineNumberEnabled;
        codeView.setEnableRelativeLineNumber(isRelativeLineNumberEnabled);
    }

    private void launchEditorButtonSheet() {
        final BottomSheetDialog dialog = new BottomSheetDialog(context.getApplicationContext());
        dialog.setContentView(R.layout.bottom_sheet_dialog);
        dialog.getWindow().setDimAmount(0f);

        final EditText searchEdit = dialog.findViewById(R.id.search_edit);
        final EditText replacementEdit = dialog.findViewById(R.id.replacement_edit);

        final ImageButton findPrevAction = dialog.findViewById(R.id.find_prev_action);
        final ImageButton findNextAction = dialog.findViewById(R.id.find_next_action);
        final ImageButton replacementAction = dialog.findViewById(R.id.replace_action);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().trim();
                if (text.isEmpty()) codeView.clearMatches();
                codeView.findMatches(Pattern.quote(text));
            }
        });

        findPrevAction.setOnClickListener(v -> {
            codeView.findPrevMatch();
        });

        findNextAction.setOnClickListener(v -> {
            codeView.findNextMatch();
        });

        replacementAction.setOnClickListener(v -> {
            String regex = searchEdit.getText().toString();
            String replacement = replacementEdit.getText().toString();
            codeView.replaceAllMatches(regex, replacement);
        });

        dialog.setOnDismissListener(c -> codeView.clearMatches());
        dialog.show();
    }
}
