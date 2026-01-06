import React from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';

const PrivacyPolicy: React.FC = () => {
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Privacy Policy
        </Typography>
        <Typography variant="body1" sx={{ mb: 2 }}>
          Last updated: {new Date().toLocaleDateString()}
        </Typography>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            1. Information We Collect
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We collect information that you provide directly to us, including:
          </Typography>
          <ul>
            <li>Microsoft account information (email, name)</li>
            <li>Academic information (courses, grades)</li>
            <li>Usage data and preferences</li>
          </ul>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            2. How We Use Your Information
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We use the information we collect to:
          </Typography>
          <ul>
            <li>Provide and maintain our service</li>
            <li>Process your academic data</li>
            <li>Improve our service</li>
            <li>Communicate with you</li>
          </ul>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            3. Data Security
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We implement appropriate security measures to protect your personal information. However, no method of transmission over the Internet is 100% secure.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            4. Cookies
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We use cookies to maintain your session and improve your experience. You can control cookies through your browser settings.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            5. Third-Party Services
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We use Microsoft authentication services. Their use of your information is governed by their privacy policy.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            6. Your Rights
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            You have the right to:
          </Typography>
          <ul>
            <li>Access your personal data</li>
            <li>Correct inaccurate data</li>
            <li>Request deletion of your data</li>
            <li>Object to data processing</li>
          </ul>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            7. Contact Us
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            If you have any questions about this Privacy Policy, please contact us at:
            <br />
            Email: jrlockhart04@gmail.com
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
};

export default PrivacyPolicy; 