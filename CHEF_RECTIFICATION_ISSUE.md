# Chef Not Seeing Rectification Requests - Solution Guide

## 🔍 Problem Analysis

When you login as `chef@test.com`, you don't see rectification requests because:

1. **Default chef user** (`chef@test.com`) is assigned to `"Informatique"` sector only
2. **Rectifications are assigned** to the correct chef based on the sector/option selected  
3. **You only see rectifications** assigned to your specific email address

## ✅ Solution Options

### Option 1: Create Sector-Specific Users (Recommended)

**Step 1: Create all sector-specific chef users**
```bash
curl -X POST http://localhost:8088/auth/create-sector-test-users
```

This creates:
- `chef.informatique@test.com` (password: `password123`) - for Informatique sector
- `chef.mathématique@test.com` (password: `password123`) - for Mathématique sector  
- `chef.ml@test.com` (password: `password123`) - for ML sector
- `chef.telecommunication@test.com` (password: `password123`) - for Telecommunication sector
- `chef.gc@test.com` (password: `password123`) - for GC sector

**Step 2: Login as the correct chef for the sector**
- If rectification was created with option "ML" → Login as `chef.ml@test.com`
- If rectification was created with option "Mathématique" → Login as `chef.mathématique@test.com`
- etc.

### Option 2: Create Rectification for Informatique Sector

Since `chef@test.com` handles `"Informatique"` sector:
1. Login as an enseignant (e.g., `enseignant@test.com`)
2. Create a rectification with **Option**: `"Informatique"`
3. Login as `chef@test.com` and you should see it in pending requests

### Option 3: Debug Current Rectifications

Check what rectifications exist and their assignments:
```bash
curl http://localhost:8089/api/rectification/debug-all
```

This shows all rectifications with their assigned chefs, helping you understand the assignments.

## 🧪 Testing Steps

### Complete Test Scenario

1. **Create sector users**:
   ```bash
   curl -X POST http://localhost:8088/auth/create-sector-test-users
   ```

2. **Create a rectification as enseignant**:
   - Login as `enseignant@test.com` / `password123`
   - Go to Grade Correction
   - Create rectification with option "ML"

3. **Check rectification assignment**:
   ```bash
   curl http://localhost:8089/api/rectification/debug-all
   ```
   Should show: `chefDepartementUsername: "chef.ml@test.com"`

4. **Login as correct chef**:
   - Login as `chef.ml@test.com` / `password123`
   - Go to Rectification Management
   - You should see the ML rectification in pending requests

5. **Verify isolation**:
   - Login as `chef@test.com` 
   - You won't see the ML rectification (correct behavior)
   - Login as `chef.informatique@test.com`
   - You won't see the ML rectification (correct behavior)

## 🎯 Expected Behavior

This is actually **correct behavior**! Each chef should only see rectifications for their specific sector:

- **chef@test.com** (Informatique sector) → sees only Informatique rectifications
- **chef.ml@test.com** (ML sector) → sees only ML rectifications  
- **chef.mathématique@test.com** (Mathématique sector) → sees only Mathématique rectifications

## 🔧 Quick Fix for Testing

If you want to test with the existing `chef@test.com` user:

1. **Create an Informatique rectification**:
   - Login as enseignant
   - Create rectification with option: `"Informatique"`
   
2. **Login as chef@test.com**:
   - You should now see the rectification in pending requests

## 📊 Debug Commands

```bash
# Check what users exist
curl http://localhost:8088/auth/check-user/chef@test.com
curl http://localhost:8088/auth/check-user/chef.ml@test.com

# Check all rectifications and their assignments  
curl http://localhost:8089/api/rectification/debug-all

# Test chef assignment for different sectors
curl http://localhost:8088/api/users/chef-by-sector/Informatique
curl http://localhost:8088/api/users/chef-by-sector/ML
curl http://localhost:8088/api/users/chef-by-sector/Mathématique
```

The system is working correctly - you just need to login as the right chef for the sector of the rectification you created! 🎉
