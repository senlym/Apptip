package Avange.apptip;

import static Avange.apptip.R.*;
import static Avange.apptip.Variables.context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import Avange.apptip.plugin.CommentManager;
import Avange.apptip.plugin.SourcePositionListener;
import Avange.apptip.plugin.UndoRedoManager;
import Avange.apptip.syntax.LanguageManager;
import Avange.apptip.syntax.LanguageName;
import Avange.apptip.syntax.ThemeName;
//esta actividad es para ver cuando se haga click en el código y la reseña
public class detalle extends AppCompatActivity {
    public final boolean useModernAutoCompleteAdapter = true;
    CodeView codeView;
    TextView textView;
    public LanguageManager languageManager;
    public CommentManager commentManager;
    public UndoRedoManager undoRedoManager;
    public TextView languageNameText;
    public TextView sourcePositionText;
    public LanguageName currentLanguage = LanguageName.JAVA;
    public ThemeName currentTheme = ThemeName.MONOKAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_detalle);
        textView = findViewById(id.textView8);
        codeView = findViewById(id.detallemain1);
        languageNameText = findViewById(id.language_name_txt);
        Variables.languageNameText = languageNameText;
        sourcePositionText = findViewById(id.source_position_txt);
        Intent intent = getIntent();
        codeView.setTextIsSelectable(true);
        codeView.setKeyListener(null);
        //obteniendo del bundle los parametros
        if(intent.getExtras() != null){
            String text = (String) intent.getSerializableExtra("text");
            String [] array = text.split(";");
            if(array[0].equals("Código")){
                textView.setText(array[0]);
                codeView.setText(array[1]);
                codeView.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), color.darkGrey));
                configCodeView(codeView);
                configCodeViewPlugins(codeView, Variables.languageNameText);
            } else {
                textView.setText(array[0]);
                codeView.setText(array[1]);
                codeView.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), color.whiteCardColor));
            }
        }
    }
    private void configCodeView(CodeView codeView) {
        try{
            // Change default font to JetBrains Mono font
            Typeface jetBrainsMono = ResourcesCompat.getFont(this, font.jetbrains_mono_medium);

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
                CustomCodeViewAdapter adapter = new CustomCodeViewAdapter(this, codeList);

                // Add the odeViewAdapter to the CodeView
                codeView.setAdapter(adapter);
            } else {
                String[] languageKeywords = languageManager.getLanguageKeywords(currentLanguage);

                // Custom list item xml layout
                final int layoutId = layout.list_item_suggestion;

                // TextView id to put suggestion on it
                final int viewId = id.suggestItemTextView;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutId, viewId, languageKeywords);

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
        try{
        undoRedoManager = new UndoRedoManager(codeView);
        undoRedoManager.connect();

        Context context1 = getApplicationContext();
        configLanguageName(languageNameText);
        sourcePositionText.setText(context1.getString(string.source_position, 0, 0));
        configSourcePositionListener(codeView);
        }catch (Exception e){
            String exp = e.getStackTrace().toString();
        }
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
            Context context1 = getApplicationContext();
            SourcePositionListener sourcePositionListener = new SourcePositionListener(codeView);
            sourcePositionListener.setOnPositionChanged((line, column) -> {
                sourcePositionText.setText(context1.getString(string.source_position, line, column));
            });} catch (Exception e) {
            String exp = e.getStackTrace().toString();
        }
    }
    private void changeTheEditorLanguage(int languageId) {
        final LanguageName oldLanguage = currentLanguage;
        if (languageId == id.language_java) currentLanguage = LanguageName.JAVA;
        else if (languageId == id.language_python) currentLanguage = LanguageName.PYTHON;
        else if(languageId == id.language_go) currentLanguage = LanguageName.GO_LANG;

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
        if (themeId == id.theme_monokia) currentTheme = ThemeName.MONOKAI;
        else if (themeId == id.theme_noctics) currentTheme = ThemeName.NOCTIS_WHITE;
        else if(themeId == id.theme_five_color) currentTheme = ThemeName.FIVE_COLOR;
        else if(themeId == id.theme_orange_box) currentTheme = ThemeName.ORANGE_BOX;

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
        dialog.setContentView(layout.bottom_sheet_dialog);
        dialog.getWindow().setDimAmount(0f);

        final EditText searchEdit = dialog.findViewById(id.search_edit);
        final EditText replacementEdit = dialog.findViewById(id.replacement_edit);

        final ImageButton findPrevAction = dialog.findViewById(id.find_prev_action);
        final ImageButton findNextAction = dialog.findViewById(id.find_next_action);
        final ImageButton replacementAction = dialog.findViewById(id.replace_action);

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