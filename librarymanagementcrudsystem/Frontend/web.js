
// ======================================
// 1. API CONFIGURATION
// ======================================

const API_URL = "http://localhost:8080/api/library";


// ======================================
// 2. DOM ELEMENTS
// ======================================

const message = document.getElementById("message");

const bookForm = document.getElementById("book-form");
const updateForm = document.getElementById("update-form");

const booksTableBody = document.getElementById("books-table-body");
const recentBooks = document.getElementById("recent-books");
const deletedTableBody = document.getElementById("deleted-table-body");

const findResult = document.getElementById("find-result");

let activeBooks = [];
let deletedBooks = [];


// ======================================
// 3. API HELPER FUNCTION
// ======================================

async function apiRequest(endpoint, options = {}) {

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        }
    });

    if (!response.ok) {

        let errorMessage = `Request failed (${response.status})`;

        try {
            const errorData = await response.json();

            errorMessage =
                errorData.message ||
                errorData.error ||
                errorData.detail ||
                errorMessage;

        } catch {
            // Response may not contain JSON.
        }

        throw new Error(errorMessage);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return response.json();
    }

    return null;
}


// ======================================
// 4. MESSAGE FUNCTION
// ======================================

function showMessage(text, type = "success") {

    message.textContent = text;

    message.className = `message ${type}`;

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });

    setTimeout(() => {
        message.classList.add("hidden");
    }, 4000);
}


// ======================================
// 5. HTML ESCAPING
// ======================================

function escapeHTML(value) {

    return String(value ?? "").replace(/[&<>"']/g, character => {

        const entities = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return entities[character];
    });
}


// ======================================
// 6. PAGE NAVIGATION
// ======================================

function showView(viewName) {

    document.querySelectorAll(".view").forEach(view => {
        view.classList.remove("active");
    });

    document.querySelectorAll("[data-view]").forEach(button => {
        button.classList.remove("active");
    });

    const selectedView = document.getElementById(viewName);

    if (!selectedView) return;

    selectedView.classList.add("active");

    document.querySelectorAll(`[data-view="${viewName}"]`).forEach(button => {
        button.classList.add("active");
    });

    const titles = {
        dashboard: "Dashboard",
        books: "All Books",
        add: "Add Book",
        find: "Find Book",
        update: "Update Book",
        recycle: "Recycle Bin"
    };

    document.getElementById("page-title").textContent =
        titles[viewName] || "LibraryHub";

    message.classList.add("hidden");

    if (viewName === "dashboard") {
        loadDashboard();
    }

    if (viewName === "books") {
        loadAllBooks();
    }

    if (viewName === "recycle") {
        loadDeletedBooks();
    }

    if (viewName === "add") {
        resetAddForm();
    }

    if (viewName === "update") {
        resetUpdateForm();
    }
}


document.querySelectorAll("[data-view]").forEach(button => {

    button.addEventListener("click", () => {

        showView(button.dataset.view);

    });

});


// ======================================
// 7. CREATE BOOK
// ======================================

bookForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const book = {

        name: document.getElementById("book-name").value.trim(),

        isbnNo: Number(document.getElementById("isbn").value),

        price: Number(document.getElementById("price").value),

        availableStock: Number(document.getElementById("stock").value)

    };

    if (!validateBook(book)) return;

    try {

        const result = await apiRequest("/create", {

            method: "POST",

            body: JSON.stringify(book)

        });

        bookForm.reset();

        showMessage("Book added successfully!");

        loadDashboard();

    } catch (error) {

        showMessage(error.message, "error");

    }

});


// ======================================
// 8. VALIDATE BOOK
// ======================================

function validateBook(book) {

    if (!book.name) {

        showMessage("Book name is required.", "error");

        return false;
    }

    if (!Number.isSafeInteger(book.isbnNo) ||
        book.isbnNo <= 0 ||
        book.isbnNo > 2147483647) {

        showMessage("Enter a valid ISBN within the Java int range.", "error");

        return false;
    }

    if (!Number.isFinite(book.price) || book.price < 0) {

        showMessage("Price cannot be negative.", "error");

        return false;
    }

    if (!Number.isSafeInteger(book.availableStock) ||
        book.availableStock < 0) {

        showMessage("Stock must be a non-negative whole number.", "error");

        return false;
    }

    return true;
}


// ======================================
// 9. READ ALL ACTIVE BOOKS
// ======================================

async function loadAllBooks() {

    booksTableBody.innerHTML = `
        <tr>
            <td colspan="6" class="empty-state">Loading books...</td>
        </tr>
    `;

    try {

        activeBooks = await apiRequest("/Books");

        renderBooks(activeBooks);

    } catch (error) {

        booksTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">
                    Unable to load books: ${escapeHTML(error.message)}
                </td>
            </tr>
        `;

        showMessage(error.message, "error");

    }
}


// ======================================
// 10. DISPLAY BOOKS IN TABLE
// ======================================

function renderBooks(books) {

    if (!Array.isArray(books) || books.length === 0) {

        booksTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">
                    No books found.
                </td>
            </tr>
        `;

        return;
    }

    booksTableBody.innerHTML = books.map(book => `

        <tr>

            <td>${escapeHTML(book.id)}</td>

            <td>${escapeHTML(book.name)}</td>

            <td>${escapeHTML(book.isbnNo)}</td>

            <td>₹${escapeHTML(book.price)}</td>

            <td>${escapeHTML(book.availableStock)}</td>

            <td>

                <button
                    class="action-button edit-button"
                    data-action="edit"
                    data-id="${escapeHTML(book.id)}">
                    Edit
                </button>

                <button
                    class="action-button delete-button"
                    data-action="delete"
                    data-id="${escapeHTML(book.id)}">
                    Delete
                </button>

            </td>

        </tr>

    `).join("");

}


// ======================================
// 11. SEARCH BOOKS IN TABLE
// ======================================

document.getElementById("book-search")
    .addEventListener("input", function () {

        const searchValue = this.value.toLowerCase().trim();

        const filteredBooks = activeBooks.filter(book => {

            return String(book.name ?? "")
                    .toLowerCase()
                    .includes(searchValue) ||

                String(book.isbnNo ?? "")
                    .includes(searchValue);

        });

        renderBooks(filteredBooks);

    });


// ======================================
// 12. EDIT BUTTON
// ======================================

booksTableBody.addEventListener("click", async function (event) {

    const button = event.target.closest("button[data-action]");

    if (!button) return;

    const id = button.dataset.id;

    if (button.dataset.action === "edit") {

        document.getElementById("update-id").value = id;

        showView("update");

        await loadBookForUpdate(id);

    }

    if (button.dataset.action === "delete") {

        await deleteBook(id);

    }

});


// ======================================
// 13. FIND BOOK BY ID
// ======================================

document.getElementById("find-id-button")
    .addEventListener("click", async function () {

        const id = document.getElementById("find-id").value.trim();

        if (!id || !/^[1-9]\d*$/.test(id)) {

            showMessage("Please enter a valid book ID.", "error");

            return;
        }

        try {

            const book = await apiRequest(`/id/${encodeURIComponent(id)}`);

            displayFindResult(book);

        } catch (error) {

            findResult.classList.remove("hidden");

            findResult.innerHTML = `
                <p class="empty-state">
                    ${escapeHTML(error.message)}
                </p>
            `;

        }

    });


// ======================================
// 14. FIND BOOK BY ISBN
// ======================================

document.getElementById("find-isbn-button")
    .addEventListener("click", async function () {

        const isbn = document.getElementById("find-isbn").value.trim();

        if (!isbn || !/^[1-9]\d*$/.test(isbn) ||
            Number(isbn) > 2147483647) {

            showMessage("Please enter a valid ISBN.", "error");

            return;
        }

        try {

            const book = await apiRequest(`/isbn/${encodeURIComponent(isbn)}`);

            displayFindResult(book);

        } catch (error) {

            findResult.classList.remove("hidden");

            findResult.innerHTML = `
                <p class="empty-state">
                    ${escapeHTML(error.message)}
                </p>
            `;

        }

    });


// ======================================
// 15. DISPLAY SEARCH RESULT
// ======================================

function displayFindResult(book) {

    findResult.classList.remove("hidden");

    findResult.innerHTML = `

        <div class="result-card">

            <h3>Book Details</h3>

            <p><strong>Book ID:</strong> ${escapeHTML(book.id)}</p>

            <p><strong>Book Name:</strong> ${escapeHTML(book.name)}</p>

            <p><strong>ISBN:</strong> ${escapeHTML(book.isbnNo)}</p>

            <p><strong>Price:</strong> ₹${escapeHTML(book.price)}</p>

            <p><strong>Available Stock:</strong> ${escapeHTML(book.availableStock)}</p>

            <button
                class="primary-button"
                id="result-edit-button"
                data-id="${escapeHTML(book.id)}">
                Edit Book
            </button>

        </div>

    `;

}


// ======================================
// 16. EDIT FROM FIND RESULT
// ======================================

findResult.addEventListener("click", async function (event) {

    const button = event.target.closest("#result-edit-button");

    if (!button) return;

    const id = button.dataset.id;

    document.getElementById("update-id").value = id;

    showView("update");

    await loadBookForUpdate(id);

});


// ======================================
// 17. LOAD BOOK FOR UPDATE
// ======================================

document.getElementById("load-update-button")
    .addEventListener("click", async function () {

        const id = document.getElementById("update-id").value.trim();

        if (!id || !/^[1-9]\d*$/.test(id)) {

            showMessage("Please enter a valid book ID.", "error");

            return;
        }

        await loadBookForUpdate(id);

    });


async function loadBookForUpdate(id) {

    try {

        const book = await apiRequest(`/id/${encodeURIComponent(id)}`);

        document.getElementById("update-id").value = book.id;

        document.getElementById("update-name").value = book.name;

        document.getElementById("update-isbn").value = book.isbnNo;

        document.getElementById("update-price").value = book.price;

        document.getElementById("update-stock").value = book.availableStock;

        updateForm.classList.remove("hidden");

        showMessage("Book loaded successfully.");

    } catch (error) {

        updateForm.classList.add("hidden");

        showMessage(error.message, "error");

    }

}


// ======================================
// 18. UPDATE BOOK
// ======================================

updateForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const id = document.getElementById("update-id").value.trim();

    const updatedBook = {

        name: document.getElementById("update-name").value.trim(),

        isbnNo: Number(document.getElementById("update-isbn").value),

        price: Number(document.getElementById("update-price").value),

        availableStock: Number(document.getElementById("update-stock").value)

    };

    if (!validateBook(updatedBook)) return;

    try {

        await apiRequest(`/update/${encodeURIComponent(id)}`, {

            method: "PATCH",

            body: JSON.stringify(updatedBook)

        });

        resetUpdateForm();

        showMessage("Book updated successfully!");

        loadDashboard();

    } catch (error) {

        showMessage(error.message, "error");

    }

});


// ======================================
// 19. CANCEL UPDATE
// ======================================

document.getElementById("update-cancel")
    .addEventListener("click", function () {

        resetUpdateForm();

        showView("books");

    });


function resetUpdateForm() {

    updateForm.reset();

    updateForm.classList.add("hidden");

}


// ======================================
// 20. SOFT DELETE BOOK
// ======================================

async function deleteBook(id) {

    const confirmDelete = confirm(
        "Are you sure you want to move this book to the Recycle Bin?"
    );

    if (!confirmDelete) return;

    try {

        await apiRequest(`/delete/${encodeURIComponent(id)}`, {

            method: "DELETE"

        });

        showMessage("Book moved to Recycle Bin successfully!");

        loadAllBooks();

        loadDashboard();

    } catch (error) {

        showMessage(error.message, "error");

    }

}


// ======================================
// 21. LOAD DELETED BOOKS
// ======================================

async function loadDeletedBooks() {

    deletedTableBody.innerHTML = `
        <tr>
            <td colspan="6" class="empty-state">Loading deleted books...</td>
        </tr>
    `;

    try {

        deletedBooks = await apiRequest("/deletedBooks");

        renderDeletedBooks(deletedBooks);

    } catch (error) {

        deletedTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">
                    ${escapeHTML(error.message)}
                </td>
            </tr>
        `;

        showMessage(error.message, "error");

    }

}


// ======================================
// 22. DISPLAY DELETED BOOKS
// ======================================

function renderDeletedBooks(books) {

    if (!Array.isArray(books) || books.length === 0) {

        deletedTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">
                    Recycle Bin is empty.
                </td>
            </tr>
        `;

        return;
    }

    deletedTableBody.innerHTML = books.map(book => `

        <tr>

            <td>${escapeHTML(book.id)}</td>

            <td>${escapeHTML(book.name)}</td>

            <td>${escapeHTML(book.isbnNo)}</td>

            <td>₹${escapeHTML(book.price)}</td>

            <td>${escapeHTML(book.availableStock)}</td>

            <td>

                <button
                    class="action-button restore-button"
                    data-action="restore"
                    data-id="${escapeHTML(book.id)}">
                    Restore
                </button>

            </td>

        </tr>

    `).join("");

}


// ======================================
// 23. RESTORE DELETED BOOK
// ======================================

deletedTableBody.addEventListener("click", async function (event) {

    const button = event.target.closest("button[data-action='restore']");

    if (!button) return;

    const id = button.dataset.id;

    const confirmRestore = confirm(
        "Do you want to restore this book?"
    );

    if (!confirmRestore) return;

    try {

        await apiRequest(`/restore/${encodeURIComponent(id)}`, {

            method: "PUT"

        });

        showMessage("Book restored successfully!");

        loadDeletedBooks();

        loadDashboard();

    } catch (error) {

        showMessage(error.message, "error");

    }

});


// ======================================
// 24. REFRESH RECYCLE BIN
// ======================================

document.getElementById("refresh-deleted")
    .addEventListener("click", loadDeletedBooks);


// ======================================
// 25. DASHBOARD STATISTICS
// ======================================

async function loadDashboard() {

    try {

        const [active, deleted] = await Promise.all([

            apiRequest("/Books"),

            apiRequest("/deletedBooks")

        ]);

        if (!Array.isArray(active) || !Array.isArray(deleted)) {
            throw new Error("Invalid response from server.");
        }

        activeBooks = active;

        deletedBooks = deleted;

        const totalStock = active.reduce((sum, book) => {

            return sum + Number(book.availableStock || 0);

        }, 0);

        const inventoryValue = active.reduce((sum, book) => {

            return sum +
                (Number(book.price || 0) *
                    Number(book.availableStock || 0));

        }, 0);


        document.getElementById("active-count").textContent =
            active.length;

        document.getElementById("deleted-count").textContent =
            deleted.length;

        document.getElementById("stock-count").textContent =
            totalStock;

        document.getElementById("inventory-value").textContent =
            "₹" + inventoryValue.toLocaleString("en-IN", {
                maximumFractionDigits: 2
            });


        // Show latest five books.

        const recent = [...active]
            .sort((a, b) => Number(b.id) - Number(a.id))
            .slice(0, 5);

        if (recent.length === 0) {

            recentBooks.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state">
                        No books available.
                    </td>
                </tr>
            `;

            return;
        }

        recentBooks.innerHTML = recent.map(book => `

            <tr>

                <td>${escapeHTML(book.id)}</td>

                <td>${escapeHTML(book.name)}</td>

                <td>${escapeHTML(book.isbnNo)}</td>

                <td>₹${escapeHTML(book.price)}</td>

                <td>${escapeHTML(book.availableStock)}</td>

            </tr>

        `).join("");

    } catch (error) {

        showMessage(
            "Dashboard could not load: " + error.message,
            "error"
        );

    }

}


// ======================================
// 26. RESET ADD FORM
// ======================================

function resetAddForm() {

    bookForm.reset();

    document.getElementById("submit-button").textContent =
        "Add Book";

}


// ======================================
// 27. CANCEL ADD BOOK
// ======================================

document.getElementById("cancel-button")
    .addEventListener("click", function () {

        resetAddForm();

        showView("dashboard");

    });


// ======================================
// 28. INITIAL APPLICATION LOAD
// ======================================

document.addEventListener("DOMContentLoaded", function () {

    showView("dashboard");

});
