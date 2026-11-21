FONTS FOR SASSY'S CREATION
==========================

Bundled here (loaded automatically by the @font-face lines at the top of ../styles.css,
so they show up in Scene Builder and in the running app with nothing to install):
  Poppins          Regular, Bold, Italic, Bold Italic   -> all headings
  Poppins Medium   Medium                               -> big numbers + brand name
  Inter            Regular, Bold                        -> body text, tables, forms

NOT bundled: SF Pro Rounded
  It is an Apple font and its licence does not allow shipping it with a Windows app.
  Buttons, nav, badges, dialog messages and error text ask for "SF Pro Rounded".
  - If you install it on your PC, Scene Builder and the app will use it.
  - If you don't, JavaFX falls back to the default system font. To use another rounded font
    instead, change the family name in the rule marked "<<< ROUNDED FONT" in styles.css.

Keep the folder layout:   styles.css   fonts/   *.fxml   all in the SAME folder level
(styles.css refers to  fonts/Poppins-Regular.ttf  relative to itself).

Scene Builder tip: after replacing the files, close and reopen the FXML
(or use  Preview > Scene Style Sheets  to confirm styles.css is attached).

If you ever run the app and the fonts still do not appear, add this ONCE in your main class,
before the first scene is shown (this is the standard JavaFX way and always works at runtime):

    for (String f : new String[]{"Poppins-Regular","Poppins-Bold","Poppins-Italic","Poppins-BoldItalic",
                                  "Poppins-Medium","Inter-Regular","Inter-Bold"}) {
        javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/fonts/" + f + ".ttf"), 12);
    }
(adjust "/fonts/" to wherever the fonts folder ends up on your classpath)
