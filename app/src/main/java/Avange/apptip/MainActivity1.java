package Avange.apptip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import Avange.apptip.plugin.CommentManager;
import Avange.apptip.plugin.SourcePositionListener;
import Avange.apptip.plugin.UndoRedoManager;
import Avange.apptip.syntax.LanguageManager;
import Avange.apptip.syntax.LanguageName;
import Avange.apptip.syntax.ThemeName;

public class MainActivity1 extends AppCompatActivity {

    private CodeView codeView;
    private DatabaseReference mDatabase;
    boolean buttonsVisible = false;
    private LanguageManager languageManager;
    private CommentManager commentManager;
    private UndoRedoManager undoRedoManager;
    private TextView languageNameText;
    private TextView sourcePositionText;
    private LanguageName currentLanguage = LanguageName.JAVA;
    private ThemeName currentTheme = ThemeName.MONOKAI;
    private MultiAutoCompleteTextView breveResena;
    ArrayList<CharSequence> arrayListCollection = new ArrayList<>();
    ArrayAdapter<CharSequence> adapter;
    private final boolean useModernAutoCompleteAdapter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        FloatingActionButton salvar = findViewById(R.id.floatingActionButton4);
        FloatingActionButton cancelar = findViewById(R.id.floatingActionButton3);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        breveResena = findViewById(R.id.textBreveResena);
        try{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(!Variables.host.equals("0.0.0.0")) database.useEmulator(Variables.host, 9015);
        mDatabase = database.getReference();
        }catch (Exception exp){
            String e = exp.getStackTrace().toString();
        }
        //region Configurando el alertdialog para el Título del Tip
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);
        final EditText editTextName1 = new EditText(this);
        editTextName1.setHint("Escriba el título de su Tip");
        alertName.setTitle("Título del Tip");
        alertName.setView(editTextName1);
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(editTextName1); // mostrar el edit en el dialog
        alertName.setView(layoutName);
        alertName.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Variables.currentTitle = editTextName1.getText().toString(); // variable para almacenar la entrada
            }
        });
        alertName.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel(); // cerrar dialog
                startActivity(new Intent(MainActivity1.this,MainActivity.class));
                finish();
            }
        });
        alertName.show(); // mostrar the dialog;
        //endregion
        //region Configurando los botones flotantes
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!buttonsVisible) {
                        salvar.setVisibility(View.VISIBLE);
                        cancelar.setVisibility(View.VISIBLE);
                        buttonsVisible = true;
                    } else {
                        salvar.setVisibility(View.INVISIBLE);
                        cancelar.setVisibility(View.INVISIBLE);
                        buttonsVisible = false;
                    }
                }catch (Exception e){
                    String dd = e.getStackTrace().toString();
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity1.this,MainActivity.class));
                finish();
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                salvar.setVisibility(View.INVISIBLE);
                cancelar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                if(Variables.logging){
                    TipModel usertip = new TipModel();
                    usertip.setCodigo(codeView.getTextWithoutTrailingSpace());
                    usertip.setCreated(Utils.fecha("yyyy-MM-dd HH:mm:ss"));
                    usertip.setIduser(Variables.user.userid);
                    usertip.setLastaccess(Utils.fecha("yyyy-MM-dd HH:mm:ss"));
                    usertip.setLikes("");
                    usertip.setResena(breveResena.getText().toString());
                    usertip.setTitle(Variables.currentTitle);
                    usertip.setUserName(Variables.user.username);
                    usertip.idtip = mDatabase.push().getKey();
                    DB db = new DB();
                    db.storeTipModel(getApplicationContext(),usertip,mDatabase);
                }
            }catch (Exception e){
                Log.e("Database",e.getStackTrace().toString());
                    salvar.setVisibility(View.INVISIBLE);
                    cancelar.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_MOVE:
                                view.setX(event.getRawX() - 120);
                                view.setY(event.getRawY() - 425);
                                break;
                            case MotionEvent.ACTION_UP:
                                view.setOnTouchListener(null);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });
        configCodeView();
        configCodeViewPlugins();
        //endregion
    }
    private void configCodeView() {
        codeView = findViewById(R.id.codeView);

        // Change default font to JetBrains Mono font
        Typeface jetBrainsMono = ResourcesCompat.getFont(this, R.font.jetbrains_mono_medium);
        codeView.setTypeface(jetBrainsMono);

        // Setup Line number feature
        codeView.setEnableLineNumber(true);
        codeView.setLineNumberTextColor(Color.GRAY);
        codeView.setLineNumberTextSize(25f);

        // Setup highlighting current line
        codeView.setEnableHighlightCurrentLine(true);
        codeView.setHighlightCurrentLineColor(Color.GRAY);

        // Setup Auto indenting feature
        codeView.setTabLength(4);
        codeView.setEnableAutoIndentation(true);

        // Setup the language and theme with SyntaxManager helper class
        languageManager = new LanguageManager(this, codeView);
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

        // Setup the auto complete and auto indenting for the current language
        configLanguageAutoComplete();
        configLanguageAutoIndentation();
    }

    private void configLanguageAutoComplete() {
        if (useModernAutoCompleteAdapter) {
            // Load the code list (keywords and snippets) for the current language
            List<Code> codeList = languageManager.getLanguageCodeList(currentLanguage);

            // Use CodeViewAdapter or custom one
            CustomCodeViewAdapter adapter = new CustomCodeViewAdapter(this, codeList);

            // Add the odeViewAdapter to the CodeView
            codeView.setAdapter(adapter);
        } else {
            String[] languageKeywords = languageManager.getLanguageKeywords(currentLanguage);

            // Custom list item xml layout
            final int layoutId = R.layout.list_item_suggestion;

            // TextView id to put suggestion on it
            final int viewId = R.id.suggestItemTextView;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutId, viewId, languageKeywords);

            // Add the ArrayAdapter to the CodeView
            codeView.setAdapter(adapter);
        }
    }

    private void configLanguageAutoIndentation() {
        codeView.setIndentationStarts(languageManager.getLanguageIndentationStarts(currentLanguage));
        codeView.setIndentationEnds(languageManager.getLanguageIndentationEnds(currentLanguage));
    }

    private void configCodeViewPlugins() {
        commentManager = new CommentManager(codeView);
        configCommentInfo();

        undoRedoManager = new UndoRedoManager(codeView);
        undoRedoManager.connect();

        languageNameText = findViewById(R.id.language_name_txt);
        configLanguageName();

        sourcePositionText = findViewById(R.id.source_position_txt);
        sourcePositionText.setText(getString(R.string.source_position, 0, 0));
        configSourcePositionListener();
    }

    private void configCommentInfo() {
        commentManager.setCommentStart(languageManager.getCommentStart(currentLanguage));
        commentManager.setCommendEnd(languageManager.getCommentEnd(currentLanguage));
    }

    private void configLanguageName() {
        languageNameText.setText(currentLanguage.name().toLowerCase());
    }

    private void configSourcePositionListener() {
        SourcePositionListener sourcePositionListener = new SourcePositionListener(codeView);
        sourcePositionListener.setOnPositionChanged((line, column) -> {
            sourcePositionText.setText(getString(R.string.source_position, line, column));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int menuItemId = item.getItemId();
        final int menuGroupId = item.getGroupId();

        if (menuGroupId == R.id.group_languages) changeTheEditorLanguage(menuItemId);
        else if (menuGroupId == R.id.group_themes) changeTheEditorTheme(menuItemId);
        else if (menuItemId == R.id.findMenu) launchEditorButtonSheet();
        else if (menuItemId == R.id.comment) commentManager.commentSelected();
        else if (menuItemId == R.id.un_comment) commentManager.unCommentSelected();
        else if (menuItemId == R.id.clearText) codeView.setText("");
        else if (menuItemId == R.id.toggle_relative_line_number) toggleRelativeLineNumber();
        else if (menuItemId == R.id.undo) undoRedoManager.undo();
        else if (menuItemId == R.id.redo) undoRedoManager.redo();

        return super.onOptionsItemSelected(item);
    }

    private void changeTheEditorLanguage(int languageId) {
        final LanguageName oldLanguage = currentLanguage;
        if (languageId == R.id.language_java) currentLanguage = LanguageName.JAVA;
        else if (languageId == R.id.language_python) currentLanguage = LanguageName.PYTHON;
        else if(languageId == R.id.language_go) currentLanguage = LanguageName.GO_LANG;

        if (currentLanguage != oldLanguage) {
            languageManager.applyTheme(currentLanguage, currentTheme);
            configLanguageName();
            configLanguageAutoComplete();
            configLanguageAutoIndentation();
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
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
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


