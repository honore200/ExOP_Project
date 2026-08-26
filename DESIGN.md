---
name: Terminal Onyx
colors:
  surface: '#0d141d'
  surface-dim: '#0d141d'
  surface-bright: '#333a44'
  surface-container-lowest: '#080f18'
  surface-container-low: '#151c26'
  surface-container: '#19202a'
  surface-container-high: '#242a34'
  surface-container-highest: '#2e353f'
  on-surface: '#dce3f0'
  on-surface-variant: '#bdcab9'
  inverse-surface: '#dce3f0'
  inverse-on-surface: '#2a313b'
  outline: '#879484'
  outline-variant: '#3e4a3c'
  surface-tint: '#66df74'
  primary: '#66df74'
  on-primary: '#00390f'
  primary-container: '#27a644'
  on-primary-container: '#00320c'
  inverse-primary: '#006e25'
  secondary: '#c4c0ff'
  on-secondary: '#2a2571'
  secondary-container: '#443f8b'
  on-secondary-container: '#b5b0ff'
  tertiary: '#ffb1c2'
  on-tertiary: '#66002b'
  tertiary-container: '#ed6089'
  on-tertiary-container: '#5a0025'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#83fc8e'
  primary-fixed-dim: '#66df74'
  on-primary-fixed: '#002106'
  on-primary-fixed-variant: '#00531a'
  secondary-fixed: '#e3dfff'
  secondary-fixed-dim: '#c4c0ff'
  on-secondary-fixed: '#14095d'
  on-secondary-fixed-variant: '#413d89'
  tertiary-fixed: '#ffd9df'
  tertiary-fixed-dim: '#ffb1c2'
  on-tertiary-fixed: '#3f0018'
  on-tertiary-fixed-variant: '#8b1040'
  background: '#0d141d'
  on-background: '#dce3f0'
  surface-variant: '#2e353f'
  surface-deep: '#0a192f'
  surface-border: '#1a2a44'
  critical-red: '#ff4d4d'
  warning-orange: '#ffa500'
  info-cyan: '#00d4ff'
  text-primary: '#ffffff'
  text-secondary: '#94a3b8'
typography:
  display-xl:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  data-mono:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-xs:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  gutter: 16px
  margin-page: 24px
  component-gap: 8px
  sidebar-width: 240px
  header-height: 56px
---

## Brand & Style

The design system embodies a **Technical Industrial** aesthetic, specifically tailored for the high-density operational needs of the Owendo Mineral Port. The personality is uncompromising, precise, and authoritative, evoking the atmosphere of a modern maritime command center. 

The style is characterized by a "dark-room" philosophy: a low-light, high-contrast environment that reduces eye strain for operators during long shifts. It utilizes sharp geometry and a dense informational grid to communicate efficiency. Visual embellishments like gradients or soft shadows are discarded in favor of flat, structural surfaces and technical borders, ensuring every pixel serves a functional purpose in monitoring and logistics.

## Colors

The palette is built on a "Total Dark" foundation to prioritize data legibility and operational focus. 

- **Foundation:** The base background is a near-black navy (`#050b14`), while primary UI surfaces use a slightly lighter deep navy (`#0a192f`).
- **Brand Accents:** We draw directly from the OMP identity, using **Forest Green (#27a644)** for primary constructive actions and **Maritime Blue (#37327e)** for structural highlights and branding.
- **Semantic Logic:** Semantic colors are tuned for high luminosity against dark backgrounds. **Critical Red** is reserved for system failures or safety alerts; **Warning Orange** for operational delays; and **Info Cyan** for tracking and telemetry updates.
- **Contrast:** Typography strictly uses high-contrast white and muted slate grays to ensure a clear hierarchy without vibrating against the dark surfaces.

## Typography

**Inter** is the exclusive typeface for the design system, chosen for its exceptional tall x-height and legibility in low-light digital environments.

The typographic system is built for **Situational Awareness**. We utilize a "Monospace-Adjacent" approach for data: all numerical values in tables and dashboards should utilize Inter’s tabular lining features to ensure columns of numbers stay perfectly aligned. 

Headlines are kept compact with tight line-heights to support high-density layouts. `label-xs` is used for metadata and technical descriptors, utilizing all-caps and increased tracking to differentiate "descriptors" from "live data."

## Layout & Spacing

This design system employs a **High-Density Fluid Grid** model. The layout is designed to maximize information "above the fold" for port controllers.

- **Grid:** A 12-column system for desktop with a compact 16px gutter. 
- **Density:** We use a 4px baseline shift. Vertical padding in data rows is aggressive (8px) to allow more rows to be visible simultaneously.
- **Structure:** A fixed left-hand navigation rail (240px) provides persistent access to port modules (Vessel Tracking, Cargo Logs, Equipment Health). 
- **Breakpoints:**
    - **Desktop (1280px+):** Full 12-column visibility.
    - **Tablet (768px - 1279px):** Sidebar collapses to icons only; 8-column grid.
    - **Mobile (<767px):** Single column vertical stack; horizontal scrolling enabled specifically for data tables.

## Elevation & Depth

In this design system, depth is communicated through **Tonal Stacking** rather than traditional shadows. This maintains the "technical screen" feel.

- **Level 0:** Base Background (`#050b14`).
- **Level 1:** Content Containers (`#0a192f`). These are defined by a 1px solid border (`#1a2a44`) rather than a shadow.
- **Level 2:** Active/Hover States. Elements slightly brighten or gain a primary brand-colored border.
- **Level 3:** Overlays/Modals. These use a slightly darker backdrop blur (12px) to isolate the foreground, with a crisp, higher-contrast border to distinguish the modal from the background workspace.

Shadows, if used for extreme priority, are "Hard Shadows"—zero blur, 1px offset, acting like a secondary border.

## Shapes

The shape language is **Sharp and Engineering-Grade**. 

We utilize a **Soft (0.25rem)** roundedness as the maximum corner radius for standard UI elements like buttons and input fields. This ensures the interface feels modern without appearing "consumer-soft." 

For status indicators and specific data-chips, we use a `rounded-sm` (2px) to maintain a nearly square, "LED-indicator" look. Layout containers and panels should strictly use the 0.25rem radius to maintain a consistent structural rhythm across the dashboard.

## Components

### Buttons
- **Primary:** Solid Brand Green (`#27a644`) with black text for maximum visibility. Square edges with 2px radius.
- **Secondary:** Transparent with a Maritime Blue (`#37327e`) border and white text.
- **Destructive:** Solid Critical Red with white text, reserved for "Halt Operations" or "Delete Log."

### Data Tables
Tables are the primary component. Header cells use `label-xs` on a slightly raised navy background. Row height is fixed at 36px. Cells containing status use "Indicator Dots" (small 8px circles) in semantic colors alongside the text.

### Input Fields
Inputs are "Ghost Style"—no fill, just a 1px border (`#1a2a44`). On focus, the border changes to Maritime Blue with a subtle 1px inner glow. Labels are always persistent and small, never floating.

### KPI Cards
KPIs feature the `display-xl` font for the metric. A 2px top-border indicates the health of the metric (Green for within parameters, Red for breached).

### Chips & Badges
Rectangular with 2px radius. Backgrounds are low-opacity versions of semantic colors (e.g., 15% Red) with full-opacity text to ensure legibility without being overwhelming in high-density views.