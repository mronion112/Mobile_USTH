# DESIGN2 implementation — Editorial Pulse

`../DESIGN2.md` is the visual source of truth. This file translates it into Android/Kotlin implementation rules; do not invent competing colors, spacing or card patterns per screen.

## On-device RSS → local store → UI flow

```text
RSS/Atom feeds (user-subscribed URLs)
      ↓ HTTP fetch + XML parse on device
Local store (Room): RssSource, Article, Category, bookmark flag
      ↓ typed nullable model
Screen ViewModel / state holder
      ↓ Loading | Content | Empty | Error
Compose/XML screen renders Editorial Pulse component
```

The mobile app has no backend and no network API layer. It fetches subscribed RSS/Atom feeds directly on device, persists items locally, and renders from the local store. There is no server module in this repository. Keep stored fields intact until the ViewModel maps them to display values: `imageUrl` → image request, `link` → original-browser intent, `publishedAt` → relative time, and `null` → safe UI fallback.

## Color tokens

| UI role | Token / hex | Use |
| --- | --- | --- |
| Canvas | `surface-canvas` `#F8FAFC` | Screen scroll background |
| Elevated card | `surface-card` `#FFFFFF` | Article/source cards, dialog surface |
| Subtle field/chip | `surface-card-subtle` `#F1F5F9` | Search field, inactive chips, ghost button |
| Primary M3 | `primary` `#005C55` | Navigation and semantic primary state |
| Active action/chip | `#0F766E` | Filled CTA, active category chip, focus border |
| Primary text | `text-primary` `#0F172A` | Headline/title |
| Secondary text | `text-secondary` `#475569` | Summary, metadata body |
| Muted | `text-muted` `#94A3B8` | Inactive navigation, placeholder |
| Border | `border-subtle` `#E2E8F0` | Card/input divider |
| Focus ring | `border-focus` `#0F766E` | Input focus, selected control |
| Trending/bookmark | `secondary` `#D97706` | Flame/trending badge, saved highlight |
| Original link | `tertiary` `#0284C7` | External-read link/secondary utility |
| Active/Error/Warning | `#16A34A` / `#DC2626` / `#D97706` | Source status only |

Do not use amber or red as normal navigation color. Error color never communicates a successful fetch state.

## Type, spacing and shape

- Headlines/actions: **Plus Jakarta Sans**; body/summary/metadata: **Inter**. Bundle/download fonts through Android’s approved font mechanism and provide `sans-serif` fallback.
- Screen headline: 26sp/34sp/800 on mobile. Card title: 16–18sp/600–700. Body: 14sp/22sp. Metadata: 12sp/16sp.
- Canvas outer margin 16dp (12dp only on tight mobile); card padding 16dp; feature hero padding 20dp; vertical list gaps 12–16dp.
- Cards 16–20dp rounded; input/button 12–14dp; thumbnails 12–14dp; chips always fully pill-shaped.
- Card border: 1dp `#E2E8F0` at soft opacity plus a low ambient shadow. Do not use heavy black elevation.

## Shared component requirements

### Top and bottom navigation

- Top utility bar is clean/white or lightly frosted. Keep search and profile/utility actions visually secondary.
- Bottom navigation is fixed/elevated: 64dp plus system safe-area inset, white, top divider and four items: Home, Explore, Bookmarks, RSS Sources.
- Active icon/label uses teal; inactive uses `#94A3B8`. Icon labels use 11sp/700.

### Article cards

- **Featured card:** white card, 16:9 image with 14dp radius, source/time metadata, 18sp headline, two-line summary, bookmark control.
- **Compact card:** white card, text on the left and 72×72dp square thumbnail on the right. It must remain balanced when image is null (show subtle placeholder rather than collapse unpredictably).
- Source/category/time uses metadata colors, never competes with headline. The external `link` is not shown in a compact card.

### Inputs and actions

- Search input is 44dp height, `#F1F5F9`, rounded 12dp, leading search glyph; focus adds 2dp teal ring and white background.
- Primary CTA is teal, white Plus Jakarta Sans 14sp/600, 44–48dp high, 12dp radius.
- Ghost/cancel controls use subtle slate background and slate-700 text. `Read original article` is sky-blue utility treatment.

### Source cards and feedback

- RSS source card: white 16dp card, 36dp favicon/icon, name, URL caption, status dot and last fetch time.
- `ACTIVE`: green dot; `FETCHING`: teal spinner; `ERROR`: red dot + safe readable error message.
- Dialogs/bottom sheets are level-3 overlays with 20dp radius and spacious 16–20dp padding.

## Visual acceptance for every PR

- [ ] Uses tokens above; no arbitrary color/radius/spacing.
- [ ] Fits 360–480dp mobile width, respects bottom safe area and supports scroll.
- [ ] Null image/description/time has a deliberate placeholder/fallback.
- [ ] Touch targets are at least 44dp and icon-only controls have content descriptions.
- [ ] Screenshot demonstrates normal plus one loading/empty/error state when the screen has API data.
