# 🌌 Quantum Scientific Calculator

A high-performance, modern Java Swing application designed with a "Quantum" aesthetic. This project was developed as part of **A1 Task1** for the **Introduction to Software Engineering** course, instructed by **Dr. Mohamed El-Ramly**.

## 🚀 Features

* **Scientific Engine**: Support for trigonometric, hyperbolic, and logarithmic functions with strict domain validation (returns `NaN` for invalid inputs).
* **Statistical Suite**: Advanced analysis including Mean, Median, Mode, Variance, and Standard Deviation.
* **Dynamic Visualizations**: 
    * **Box Plots**: Visualize data distribution and quartiles.
    * **Histograms**: Frequency distribution analysis.
    * **Scatter Plots**: Cartesian coordinate data mapping.
* **Modern UI**: Built with a custom "Quantum" dark theme featuring flicker-free transitions and rounded-border navigation.

## 🛠️ Technical Architecture

### Domain Validation
Mathematical functions are wrapped with domain-safety checks to ensure stability. The engine evaluates inputs before processing to prevent calculation errors:
* `log(x)` and `ln(x)` validate $x > 0$.
* `acos(x)` and `asin(x)` validate $|x| \le 1$.
* `tan(x)` handles asymptotic limits near $\frac{\pi}{2}$ using an epsilon precision check.
* **Hyperbolic checks**: `acosh(x)` validates $x \ge 1$ and `atanh(x)` validates $|x| < 1$.



### Flicker-Free UI
The GUI overcomes standard Swing rendering limitations. By using custom `paintComponent` overrides to manually clear backgrounds and disabling `setRolloverEnabled(false)`, the application prevents "ghosting" or overlapping text when hovering over navigation elements.



## ⚠️ Current Limitations

While the calculator is robust for standard scientific use, the following limitations currently exist:
* **Real Numbers Only**: The engine does not currently support complex number calculations (e.g., $\sqrt{-1}$ returns `NaN`).
* **Static Datasets**: Statistical visualizations are optimized for single-variable datasets; multi-series comparison is not yet implemented.
* **Precision Limits**: As with all double-precision floating-point systems, extremely large exponents or values very close to zero may encounter rounding nuances.
* **Fixed Layout**: The UI is optimized for the initial window dimensions; while responsive, it is not designed for ultra-small mobile-style aspect ratios.

## ⌨️ Supported Operations

| Type | Operators / Functions |
| :--- | :--- |
| **Basic** | `+`, `-`, `*`, `/`, `%`, `^` |
| **Trig** | `sin`, `cos`, `tan`, `asin`, `acos`, `atan` |
| **Hyperbolic**| `sinh`, `cosh`, `tanh`, `asinh`, `acosh`, `atanh` |
| **Log/Exp** | `log` (base 10), `ln`, `log2`, `exp`, `10^x` |
| **Statistics**| `Mean`, `Median`, `Mode`, `Variance`, `StdDev` |

## 🏗️ Project Structure

* `core/`: The mathematical core, including the `Evaluator` and Tokenizer.
* `gui/`: The main `Window` frame and page navigation controller.
* `gui/elements/`: Custom UI components (BoxPlot, Histogram, ScatterPlot, and Data Tables).

---
**Course:** Introduction to Software Engineering  
**Instructor:** Dr. Mohamed El-Ramly  
**Assignment:** A1 Task1  
Developed by Ali Hassan
