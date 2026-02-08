UPDATE users
SET password = '$2y$10$T2vNoyPCWrPnqEP5GpblcepyV3wi9rFT.u.UHO.LN1r4OXbrEEwC2',
    role = 'ADMIN',
    enabled = TRUE
WHERE username = 'admin';