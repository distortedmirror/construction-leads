package com.constructionleads;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int NAVY = Color.rgb(23, 42, 58);
    private static final int ORANGE = Color.rgb(217, 119, 6);
    private static final int INK = Color.rgb(35, 48, 58);
    private static final int MUTED = Color.rgb(104, 119, 130);
    private static final int CANVAS = Color.rgb(246, 248, 249);

    private final List<Job> jobs = new ArrayList<>();
    private final List<String> applications = new ArrayList<>();
    private LinearLayout page;
    private LinearLayout content;
    private String activeTab = "Home";
    private String profileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seedJobs();
        render();
    }

    private void seedJobs() {
        jobs.add(new Job("Licensed electrician for kitchen remodel", "Residential · Electrical", "Austin, TX", "Looking for a licensed electrician to rough-in and finish a full kitchen remodel. Must be available next week and bring own tools.", "2–3 weeks", "Posted today"));
        jobs.add(new Job("Framing crew for 2,400 sq ft home", "New construction · Framing", "Round Rock, TX", "Seeking an experienced framing crew for a new single-family home. Plans are ready; please share crew size and recent references.", "3–4 weeks", "Posted yesterday"));
        jobs.add(new Job("Commercial drywall subcontractor", "Commercial · Drywall", "Austin, TX", "Subcontractor needed for drywall installation and finishing on a small office renovation. Insured crews preferred.", "1 week", "Posted 2 days ago"));
    }

    private void render() {
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(CANVAS);
        setContentView(page);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(22), dp(18), dp(22), dp(16));
        header.setBackgroundColor(NAVY);
        TextView brand = text("CONSTRUCTION LEADS", 12, Color.rgb(255, 190, 92), true);
        brand.setLetterSpacing(0.12f);
        header.addView(brand);
        TextView title = text(pageTitle(), 24, Color.WHITE, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(6);
        header.addView(title, titleParams);
        page.addView(header);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(24));
        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        switch (activeTab) {
            case "Jobs": buildJobs(); break;
            case "Post": buildPost(); break;
            case "Profile": buildProfile(); break;
            default: buildHome(); break;
        }
        addNavigation();
    }

    private String pageTitle() {
        if ("Jobs".equals(activeTab)) return "Find your next job";
        if ("Post".equals(activeTab)) return "Request workers";
        if ("Profile".equals(activeTab)) return "Join the network";
        return "Build your next connection";
    }

    private void buildHome() {
        LinearLayout hero = panel();
        TextView eyebrow = text("LOCAL PEOPLE. REAL PROJECTS.", 11, ORANGE, true);
        eyebrow.setLetterSpacing(0.08f);
        hero.addView(eyebrow);
        TextView headline = text("The right crew makes all the difference.", 24, NAVY, true);
        addTop(hero, headline, 10);
        TextView copy = text("Connect with clients, contractors, and skilled workers. Post a project or find your next opportunity—all in one place.", 14, MUTED, false);
        addTop(hero, copy, 8);
        Button find = button("Browse open requests", true);
        addTop(hero, find, 18);
        find.setOnClickListener(v -> changeTab("Jobs"));
        Button post = button("Post a worker request", false);
        addTop(hero, post, 8);
        post.setOnClickListener(v -> changeTab("Post"));
        content.addView(hero);

        LinearLayout intro = rowTitle("Recent requests", "See all", () -> changeTab("Jobs"));
        addTop(content, intro, 22);
        for (int i = 0; i < Math.min(2, jobs.size()); i++) addJobCard(jobs.get(i));

        LinearLayout join = panel();
        TextView joinTitle = text(profileName.isEmpty() ? "Work in construction?" : "Welcome, " + profileName + "!", 18, NAVY, true);
        join.addView(joinTitle);
        addTop(join, text("Create a simple profile to share your business, service area, and contact details with the community.", 14, MUTED, false), 7);
        Button profile = button(profileName.isEmpty() ? "Create your profile" : "Edit your profile", false);
        addTop(join, profile, 14);
        profile.setOnClickListener(v -> changeTab("Profile"));
        addTop(content, join, 18);
        addTop(content, text("Prototype preview · Sample requests are stored only on this device session.", 12, MUTED, false), 18);
    }

    private void buildJobs() {
        TextView explainer = text("Browse requests from clients looking for construction professionals. Tap a request to share your interest.", 14, MUTED, false);
        content.addView(explainer);
        EditText search = input("Search trade, project, or location", false);
        addTop(content, search, 16);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        addTop(content, list, 4);
        Runnable refresh = () -> {
            list.removeAllViews();
            String query = search.getText().toString().toLowerCase(Locale.US).trim();
            int count = 0;
            for (Job job : jobs) {
                String searchable = (job.title + " " + job.category + " " + job.location + " " + job.description).toLowerCase(Locale.US);
                if (query.isEmpty() || searchable.contains(query)) {
                    addJobCard(list, job);
                    count++;
                }
            }
            if (count == 0) list.addView(text("No requests match that search yet.", 14, MUTED, false));
        };
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refresh.run(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        refresh.run();
    }

    private void buildPost() {
        content.addView(text("Tell the network what kind of help your project needs. This prototype keeps requests on this device; no account or server is connected.", 14, MUTED, false));
        EditText title = input("Request title (e.g. Tile installer for bathroom)", false);
        EditText trade = input("Trade or work category", false);
        EditText location = input("City, state", false);
        EditText description = input("Describe the job, skills needed, timing, and any requirements", true);
        EditText duration = input("Estimated duration (optional)", false);
        addTop(content, title, 16);
        addTop(content, trade, 10);
        addTop(content, location, 10);
        addTop(content, description, 10);
        addTop(content, duration, 10);
        Button submit = button("Publish request", true);
        addTop(content, submit, 18);
        submit.setOnClickListener(v -> {
            if (value(title).isEmpty() || value(trade).isEmpty() || value(location).isEmpty() || value(description).isEmpty()) {
                Toast.makeText(this, "Add a title, category, location, and job description.", Toast.LENGTH_LONG).show();
                return;
            }
            jobs.add(0, new Job(value(title), value(trade), value(location), value(description), value(duration).isEmpty() ? "Flexible" : value(duration), "Just posted"));
            Toast.makeText(this, "Request added to this prototype.", Toast.LENGTH_SHORT).show();
            changeTab("Jobs");
        });
    }

    private void buildProfile() {
        content.addView(text("Create a basic introduction for clients and other professionals. Profile details are kept in memory for this prototype only.", 14, MUTED, false));
        Spinner role = new Spinner(this);
        String[] roles = {"General contractor", "Subcontractor", "Construction worker", "Client seeking workers"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        role.setAdapter(adapter);
        role.setPadding(dp(12), dp(4), dp(12), dp(4));
        role.setBackground(roundRect(Color.WHITE, Color.rgb(222, 228, 232), 10));
        EditText name = input("Your name", false);
        if (!profileName.isEmpty()) name.setText(profileName);
        EditText business = input("Business name (optional)", false);
        EditText specialty = input("Trade or specialty", false);
        EditText address = input("Street address (optional)", false);
        EditText city = input("City, state, ZIP", false);
        EditText phone = input("Phone number", false);
        EditText email = input("Email address", false);
        EditText about = input("A short note about your experience or needs", true);
        addTop(content, label("I am joining as"), 16);
        addTop(content, role, 5);
        addTop(content, name, 12);
        addTop(content, business, 10);
        addTop(content, specialty, 10);
        addTop(content, address, 10);
        addTop(content, city, 10);
        addTop(content, phone, 10);
        addTop(content, email, 10);
        addTop(content, about, 10);
        Button save = button("Save profile", true);
        addTop(content, save, 18);
        save.setOnClickListener(v -> {
            if (value(name).isEmpty() || value(city).isEmpty() || (value(phone).isEmpty() && value(email).isEmpty())) {
                Toast.makeText(this, "Please add your name, city, and a phone number or email.", Toast.LENGTH_LONG).show();
                return;
            }
            profileName = value(name);
            Toast.makeText(this, "Profile saved for this session.", Toast.LENGTH_SHORT).show();
            changeTab("Home");
        });
    }

    private void addJobCard(Job job) { addJobCard(content, job); }

    private void addJobCard(LinearLayout parent, Job job) {
        LinearLayout card = panel();
        TextView tag = text(job.category.toUpperCase(Locale.US), 10, ORANGE, true);
        tag.setLetterSpacing(0.04f);
        card.addView(tag);
        addTop(card, text(job.title, 18, NAVY, true), 7);
        addTop(card, text("⌖  " + job.location + "     ·     " + job.duration, 13, MUTED, false), 7);
        addTop(card, text(job.description, 14, INK, false), 10);
        TextView posted = text(job.posted, 12, MUTED, false);
        card.addView(posted);
        Button respond = button("I'm interested", false);
        addTop(card, respond, 12);
        respond.setOnClickListener(v -> showInterestDialog(job));
        addTop(parent, card, 12);
    }

    private void showInterestDialog(Job job) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(4), dp(8), dp(4), 0);
        EditText specialty = input("Your specialty / relevant experience", false);
        EditText contact = input("Phone or email", false);
        EditText notes = input("Notes for the client (availability, crew size, etc.)", true);
        form.addView(specialty);
        addTop(form, contact, 9);
        addTop(form, notes, 9);
        new AlertDialog.Builder(this)
                .setTitle("Respond to request")
                .setMessage(job.title)
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send interest", (dialog, which) -> {
                    if (value(specialty).isEmpty() || value(contact).isEmpty()) {
                        Toast.makeText(this, "Add your specialty and contact information to respond.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    applications.add(job.title + " — " + value(specialty) + " / " + value(contact) + " / " + value(notes));
                    Toast.makeText(this, "Your interest was recorded in this session.", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void addNavigation() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(4), dp(8), dp(4), dp(8));
        nav.setBackgroundColor(Color.WHITE);
        String[] tabs = {"Home", "Jobs", "Post", "Profile"};
        for (String tab : tabs) {
            TextView item = text(tab, 12, activeTab.equals(tab) ? ORANGE : MUTED, activeTab.equals(tab));
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(6), dp(8), dp(6), dp(8));
            item.setOnClickListener(v -> changeTab(tab));
            nav.addView(item, new LinearLayout.LayoutParams(0, -2, 1));
        }
        page.addView(nav);
    }

    private LinearLayout rowTitle(String title, String action, Runnable onClick) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = text(title, 19, NAVY, true);
        row.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        TextView link = text(action + "  ›", 13, ORANGE, true);
        link.setOnClickListener(v -> onClick.run());
        row.addView(link);
        return row;
    }

    private LinearLayout panel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackground(roundRect(Color.WHITE, Color.rgb(232, 236, 239), 14));
        return box;
    }

    private EditText input(String hint, boolean multiline) {
        EditText field = new EditText(this);
        field.setTextSize(14);
        field.setTextColor(INK);
        field.setHintTextColor(MUTED);
        field.setHint(hint);
        field.setSingleLine(!multiline);
        if (multiline) {
            field.setMinLines(3);
            field.setGravity(Gravity.TOP | Gravity.START);
        }
        field.setPadding(dp(12), dp(11), dp(12), dp(11));
        field.setBackground(roundRect(Color.WHITE, Color.rgb(222, 228, 232), 10));
        return field;
    }

    private Button button(String text, boolean primary) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(primary ? Color.WHITE : NAVY);
        button.setPadding(dp(14), dp(4), dp(14), dp(4));
        button.setBackground(roundRect(primary ? ORANGE : Color.rgb(255, 247, 235), primary ? ORANGE : Color.rgb(255, 230, 194), 10));
        return button;
    }

    private TextView label(String value) { return text(value, 13, INK, true); }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable roundRect(int color, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private void addTop(LinearLayout parent, View child, int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(margin);
        parent.addView(child, params);
    }

    private String value(EditText field) { return field.getText().toString().trim(); }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }

    private void changeTab(String tab) {
        activeTab = tab;
        render();
    }

    private static class Job {
        final String title, category, location, description, duration, posted;
        Job(String title, String category, String location, String description, String duration, String posted) {
            this.title = title;
            this.category = category;
            this.location = location;
            this.description = description;
            this.duration = duration;
            this.posted = posted;
        }
    }
}
